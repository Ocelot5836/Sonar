package io.github.ocelot.sonar.model;

import net.minecraft.client.model.Model;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class ModelConverter117
{
    public static void main(String[] args)
    {
        convert(new YetiModel());
    }

    private static <T extends Model & ConverterModel> void convert(T model)
    {
        Map<ModelConverterPart, String> rendererMapping = mapRenderers(model);

        StringBuilder builder = new StringBuilder("\tpublic static TexturedModelData getTexturedModelData(Dilation dilation) {\n\t\tModelData modelData = new ModelData();\n\t\tModelPartData rootData = modelData.getRoot();\n");

        AtomicInteger nextVariableIndex = new AtomicInteger(1);
        AtomicInteger nextPartName = new AtomicInteger(1);
        addChildren(part -> rendererMapping.computeIfAbsent(part, key -> "part" + nextPartName.getAndIncrement()), builder, "rootData", model.getRootParts(), nextVariableIndex);
        builder.append("\t\treturn TexturedModelData.of(modelData, ").append(model.texWidth).append(", ").append(model.texHeight).append(");\n\t}");

        System.out.println("Result:\n" + builder);
    }

    private static void addChildren(Function<ModelConverterPart, String> rendererMapping, StringBuilder builder, String fieldName, Collection<ModelConverterPart> parts, AtomicInteger nextVariableIndex)
    {
        for (ModelConverterPart part : parts)
        {
            List<ModelConverterPart> children = part.children();
            int index = nextVariableIndex.get();
            builder.append("\t\t");
            if (!children.isEmpty())
            {
                nextVariableIndex.getAndIncrement();
                builder.append("ModelPartData data").append(index).append(" = ");
            }
            builder.append(fieldName).append(".addChild(\"").append(rendererMapping.apply(part)).append("\", ");
            part.builderLine(builder);
            if (part.xRot != 0 || part.yRot != 0 || part.zRot != 0)
            {
                if (part.x != 0 || part.y != 0 || part.z != 0)
                {
                    builder.append(", ModelTransform.of(").append(part.x).append("F, ").append(part.y).append("F, ").append(part.z).append("F, ").append(part.xRot).append("F, ").append(part.yRot).append("F, ").append(part.zRot).append("F)");
                }
                else
                {
                    builder.append(", ModelTransform.rotation(").append(part.xRot).append("F, ").append(part.yRot).append("F, ").append(part.zRot).append("F)");
                }
            }
            else
            {
                builder.append(", ModelTransform.pivot(").append(part.x).append("F, ").append(part.y).append("F, ").append(part.z).append("F)");
            }
            builder.append(");\n");
            if (!children.isEmpty())
            {
                addChildren(rendererMapping, builder, "data" + index, part.children(), nextVariableIndex);
            }
        }
    }

    private static Map<ModelConverterPart, String> mapRenderers(Model model)
    {
        Map<ModelConverterPart, String> renderers = new HashMap<>();
        Class<?> i = model.getClass();
        while (i != null && i != Object.class)
        {
            for (Field field : i.getDeclaredFields())
            {
                if (!field.isSynthetic())
                {
                    if (ModelConverterPart.class.isAssignableFrom(field.getType()))
                    {
                        try
                        {
                            field.setAccessible(true);
                            renderers.put((ModelConverterPart) field.get(model), field.getName());
                        }
                        catch (Exception ignored)
                        {
                        }
                    }
                }
            }
            i = i.getSuperclass();
        }
        return renderers;
    }
}
