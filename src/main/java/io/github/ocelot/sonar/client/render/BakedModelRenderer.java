package io.github.ocelot.sonar.client.render;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Random;

/**
 * <p>Fills {@link VertexConsumer} with {@link BakedModel} vertex data.</p>
 *
 * @since 5.1.0
 */
public class BakedModelRenderer
{
    private static final Random RANDOM = new Random();

    /**
     * Renders the specified model into the provided buffer.
     *
     * @param model         The model to render
     * @param builder       The builder to put the model into
     * @param matrixStack   The stack of transformations to move elements
     * @param red           The amount to multiply the texture red by
     * @param green         The amount to multiply the texture green by
     * @param blue          The amount to multiply the texture blue by
     * @param packedLight   The packed uv into the light texture the parts should be rendered at
     * @param packedOverlay The packed uv into the overlay texture the parts should be rendered at
     * @param modelData     Additional forge data for model rendering. Use {@link EmptyModelData#INSTANCE} for nothing
     */
    public static void renderModel(BakedModel model, VertexConsumer builder, PoseStack matrixStack, float red, float green, float blue, int packedLight, int packedOverlay, IModelData modelData)
    {
        RANDOM.setSeed(42L);
        for (Direction direction : Direction.values())
        {
            for (BakedQuad quad : model.getQuads(null, direction, RANDOM, modelData))
            {
                builder.putBulkData(matrixStack.last(), quad, red, green, blue, packedLight, packedOverlay);
            }
        }
        for (BakedQuad quad : model.getQuads(null, null, RANDOM, modelData))
        {
            builder.putBulkData(matrixStack.last(), quad, red, green, blue, packedLight, packedOverlay);
        }
    }
}
