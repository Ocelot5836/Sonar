package io.github.ocelot.sonar.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.Entity;

import java.util.Collection;
import java.util.Collections;

public class YetiModel extends EntityModel<Entity> implements ConverterModel
{
	private final ModelConverterPart body;
	private final ModelConverterPart bone;
	private final ModelConverterPart head;
	private final ModelConverterPart bone3;
	private final ModelConverterPart bone2;
	private final ModelConverterPart arm0;
	private final ModelConverterPart arm1;
	private final ModelConverterPart leg0;
	private final ModelConverterPart leg1;

	public YetiModel() {
		this.texWidth = 256;
		this.texHeight = 256;

		body = new ModelConverterPart(this);
		body.setPos(0.0F, -11.0F, 0.0F);
		

		bone = new ModelConverterPart(this);
		bone.setPos(0.0F, 13.0F, 2.0F);
		body.addChild(bone);
		bone.texOffs(0, 43).addBox(-7.5F, -2.0F, -7.0F, 15.0F, 9.0F, 10.0F, 0.0F, false);
		bone.texOffs(80, 38).addBox(-7.5F, 7.0F, -7.0F, 15.0F, 6.0F, 10.0F, 0.0F, false);
		bone.texOffs(0, 0).addBox(-9.0F, -15.0F, -9.0F, 18.0F, 13.0F, 13.0F, 0.0F, false);
		bone.texOffs(0, 26).addBox(-9.0F, -2.0F, -9.0F, 18.0F, 4.0F, 13.0F, 0.01F, false);

		head = new ModelConverterPart(this);
		head.setPos(0.0F, 2.0F, -4.0F);
		body.addChild(head);
		head.texOffs(80, 54).addBox(-4.0F, -12.0F, -5.5F, 8.0F, 10.0F, 8.0F, 0.0F, false);
		head.texOffs(80, 54).addBox(-4.0F, -12.0F, -5.5F, 8.0F, 10.0F, 8.0F, 0.0F, false);
		head.texOffs(92, 24).addBox(-4.0F, -2.0F, -5.5F, 8.0F, 3.0F, 8.0F, 0.0F, false);
		head.texOffs(0, 43).addBox(-1.0F, -5.0F, -7.5F, 2.0F, 4.0F, 2.0F, 0.0F, false);

		bone3 = new ModelConverterPart(this);
		bone3.setPos(0.0F, -8.0F, -2.0F);
		head.addChild(bone3);
		setRotationAngle(bone3, 0.829F, 0.0F, 0.0F);
		bone3.texOffs(0, 26).addBox(4.0F, -8.0F, -2.0F, 2.0F, 8.0F, 3.0F, 0.0F, true);

		bone2 = new ModelConverterPart(this);
		bone2.setPos(0.0F, -8.0F, -2.0F);
		head.addChild(bone2);
		setRotationAngle(bone2, 0.829F, 0.0F, 0.0F);
		bone2.texOffs(0, 26).addBox(-6.0F, -8.0F, -2.0F, 2.0F, 8.0F, 3.0F, 0.0F, false);

		arm0 = new ModelConverterPart(this);
		arm0.setPos(0.0F, 1.0F, 1.0F);
		body.addChild(arm0);
		arm0.texOffs(72, 73).addBox(-16.0F, -2.5F, -5.0F, 7.0F, 30.0F, 8.0F, 0.0F, false);
		arm0.texOffs(50, 43).addBox(-16.0F, -2.5F, -5.0F, 7.0F, 30.0F, 8.0F, 0.5F, false);

		arm1 = new ModelConverterPart(this);
		arm1.setPos(0.0F, 1.0F, 1.0F);
		body.addChild(arm1);
		arm1.texOffs(72, 73).addBox(9.0F, -2.5F, -5.0F, 7.0F, 30.0F, 8.0F, 0.0F, true);
		arm1.texOffs(50, 43).addBox(9.0F, -2.5F, -5.0F, 7.0F, 30.0F, 8.0F, 0.5F, true);

		leg0 = new ModelConverterPart(this);
		leg0.setPos(-4.0F, 18.0F, -1.0F);
		body.addChild(leg0);
		leg0.texOffs(23, 98).addBox(-2.5F, 0.0F, -2.0F, 5.0F, 17.0F, 7.0F, 0.0F, false);
		leg0.texOffs(47, 91).addBox(-2.5F, 0.0F, -2.0F, 5.0F, 17.0F, 7.0F, 0.5F, false);

		leg1 = new ModelConverterPart(this);
		leg1.setPos(4.0F, 18.0F, -1.0F);
		body.addChild(leg1);
		leg1.texOffs(23, 98).addBox(-2.5F, 0.0F, -2.0F, 5.0F, 17.0F, 7.0F, 0.0F, false);
		leg1.texOffs(47, 91).addBox(-2.5F, 0.0F, -2.0F, 5.0F, 17.0F, 7.0F, 0.5F, false);
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
		return Collections.singleton(this.body);
	}

	public void setRotationAngle(ModelConverterPart modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
}