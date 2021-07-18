package io.github.ocelot.sonar.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.Entity;

import java.util.Arrays;
import java.util.Collection;

public class PirateModel extends EntityModel<Entity> implements ConverterModel
{
    private final ModelConverterPart body;
    private final ModelConverterPart head;
    private final ModelConverterPart left_leg;
    private final ModelConverterPart right_leg;
    private final ModelConverterPart left_arm;
    private final ModelConverterPart right_arm;

    public PirateModel()
    {
        this.texWidth = 64;
        this.texHeight = 64;

        body = new ModelConverterPart(this);
        body.setPos(0.0F, 24.0F, 0.0F);
        body.texOffs(22, 34).addBox(-4.0F, -24.0F, -3.0F, 8.0F, 12.0F, 6.0F, 0.0F, false);
        body.texOffs(0, 16).addBox(-4.0F, -24.0F, -3.0F, 8.0F, 18.0F, 6.0F, 0.5F, false);

        head = new ModelConverterPart(this);
        head.setPos(0.0F, 0.0F, 0.0F);
        head.texOffs(28, 16).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, 0.0F, false);
        head.texOffs(0, 0).addBox(-5.0F, -12.0F, -5.0F, 10.0F, 6.0F, 10.0F, 0.0F, false);
        head.texOffs(0, 0).addBox(-1.0F, -3.0F, -6.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);

        left_leg = new ModelConverterPart(this);
        left_leg.setPos(2.0F, 12.0F, 0.0F);
        left_leg.texOffs(40, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);

        right_leg = new ModelConverterPart(this);
        right_leg.setPos(-2.0F, 12.0F, 0.0F);
        right_leg.texOffs(40, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, true);

        left_arm = new ModelConverterPart(this);
        left_arm.setPos(5.0F, 2.0F, 0.0F);
        left_arm.texOffs(0, 40).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, true);

        right_arm = new ModelConverterPart(this);
        right_arm.setPos(-5.0F, 2.0F, 0.0F);
        right_arm.texOffs(0, 40).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);
    }

    @Override
    public void setupAnim(Entity arg, float f, float g, float h, float i, float j)
    {
    }

    @Override
    public void renderToBuffer(PoseStack arg, VertexConsumer arg2, int i, int j, float f, float g, float h, float k)
    {
    }

    @Override
    public Collection<ModelConverterPart> getRootParts()
    {
        return Arrays.asList(this.body, this.head, this.left_leg, this.right_leg, this.left_arm, this.right_arm);
    }
}
