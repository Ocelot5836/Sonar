package io.github.ocelot.sonar.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;

import java.util.Random;

/**
 * <p>Fills {@link IVertexBuilder} with {@link IBakedModel} vertex data.</p>
 *
 * @since 4.1.0
 */
public class ModelRenderer
{
    /**
     * Renders the specified model into the provided buffer.
     *
     * @param model         The model to render
     * @param builder       The builder to put the model into
     * @param matrixStack   The stack of transformations to move elements
     * @param packedLight   The packed uv into the light texture the parts should be rendered at
     * @param packedOverlay The packed uv into the overlay texture the parts should be rendered at
     */
    public static void renderModel(IBakedModel model, IVertexBuilder builder, MatrixStack matrixStack, int packedLight, int packedOverlay)
    {
        renderModel(model, builder, matrixStack, packedLight, packedOverlay, EmptyModelData.INSTANCE);
    }

    /**
     * Renders the specified model into the provided buffer.
     *
     * @param model         The model to render
     * @param builder       The builder to put the model into
     * @param matrixStack   The stack of transformations to move elements
     * @param packedLight   The packed uv into the light texture the parts should be rendered at
     * @param packedOverlay The packed uv into the overlay texture the parts should be rendered at
     * @param modelData     Additional forge data for model rendering
     */
    public static void renderModel(IBakedModel model, IVertexBuilder builder, MatrixStack matrixStack, int packedLight, int packedOverlay, IModelData modelData)
    {
        Random random = new Random(42L);
        for (Direction direction : Direction.values())
        {
            for (BakedQuad quad : model.getQuads(null, direction, random, modelData))
            {
                builder.addQuad(matrixStack.getLast(), quad, 1, 1, 1, packedLight, packedOverlay);
            }
        }
        for (BakedQuad quad : model.getQuads(null, null, random, modelData))
        {
            builder.addQuad(matrixStack.getLast(), quad, 1, 1, 1, packedLight, packedOverlay);
        }
    }
}
