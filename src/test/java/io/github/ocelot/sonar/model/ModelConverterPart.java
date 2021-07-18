package io.github.ocelot.sonar.model;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;

import java.util.List;

public class ModelConverterPart extends ModelPart
{
    private final List<RecordedCube> recordedCubes = new ObjectArrayList<>();
    private final List<ModelConverterPart> recordedChildren = new ObjectArrayList<>();

    private int xTexOffs;
    private int yTexOffs;

    public ModelConverterPart(Model arg)
    {
        super(arg);
    }

    public ModelConverterPart(Model arg, int i, int j)
    {
        super(arg, i, j);
    }

    public ModelConverterPart(int i, int j, int k, int l)
    {
        super(i, j, k, l);
    }

    @Override
    public void addChild(ModelPart part)
    {
        if (part instanceof ModelConverterPart)
            this.recordedChildren.add((ModelConverterPart) part);
    }

    @Override
    public ModelPart texOffs(int i, int j)
    {
        super.texOffs(i, j);
        this.xTexOffs = i;
        this.yTexOffs = j;
        return this;
    }

    @Override
    public ModelPart addBox(String name, float minX, float minY, float minZ, int sizeX, int sizeY, int sizeZ, float dilate, int xTexOffs, int yTexOffs)
    {
        super.addBox(name, minX, minY, minZ, sizeX, sizeY, sizeZ, dilate, xTexOffs, yTexOffs);
        this.addBox(this.xTexOffs, this.yTexOffs, minX, minY, minZ, (float) sizeX, (float) sizeY, (float) sizeZ, dilate, dilate, dilate, this.mirror);
        return this;
    }

    @Override
    public ModelPart addBox(float minX, float minY, float minZ, float sizeX, float sizeY, float sizeZ)
    {
        super.addBox(minX, minY, minZ, sizeX, sizeY, sizeZ);
        this.addBox(this.xTexOffs, this.yTexOffs, minX, minY, minZ, sizeX, sizeY, sizeZ, 0.0F, 0.0F, 0.0F, this.mirror);
        return this;
    }

    @Override
    public ModelPart addBox(float minX, float minY, float minZ, float sizeX, float sizeY, float sizeZ, boolean mirrored)
    {
        super.addBox(minX, minY, minZ, sizeX, sizeY, sizeZ, mirrored);
        this.addBox(this.xTexOffs, this.yTexOffs, minX, minY, minZ, sizeX, sizeY, sizeZ, 0.0F, 0.0F, 0.0F, mirrored);
        return this;
    }

    @Override
    public void addBox(float minX, float minY, float minZ, float sizeX, float sizeY, float sizeZ, float dilate)
    {
        super.addBox(minX, minY, minZ, sizeX, sizeY, sizeZ, dilate);
        this.addBox(this.xTexOffs, this.yTexOffs, minX, minY, minZ, sizeX, sizeY, sizeZ, dilate, dilate, dilate, this.mirror);
    }

    @Override
    public void addBox(float minX, float minY, float minZ, float sizeX, float sizeY, float sizeZ, float dilateX, float dilateY, float dilateZ)
    {
        super.addBox(minX, minY, minZ, sizeX, sizeY, sizeZ, dilateX, dilateY, dilateZ);
        this.addBox(this.xTexOffs, this.yTexOffs, minX, minY, minZ, sizeX, sizeY, sizeZ, dilateX, dilateY, dilateZ, this.mirror);
    }

    @Override
    public void addBox(float minX, float minY, float minZ, float sizeX, float sizeY, float sizeZ, float dilate, boolean mirrored)
    {
        super.addBox(minX, minY, minZ, sizeX, sizeY, sizeZ, dilate, mirrored);
        this.addBox(this.xTexOffs, this.yTexOffs, minX, minY, minZ, sizeX, sizeY, sizeZ, dilate, dilate, dilate, mirrored);
    }

    private void addBox(int texX, int texY, float minX, float minY, float minZ, float sizeX, float sizeY, float sizeZ, float dilateX, float dilateY, float dilateZ, boolean mirrored)
    {
        this.recordedCubes.add(new RecordedCube(texX, texY, minX, minY, minZ, sizeX, sizeY, sizeZ, dilateX, dilateY, dilateZ, mirrored));
    }

    /**
     * Generates a builder line to replicate this cube in 1.17
     */
    public void builderLine(StringBuilder builder)
    {
        builder.append("ModelPartBuilder.create()");
        for (int i = 0; i < 2; i++)
        {
            boolean mirror = i == 0;
            boolean writtenMirror = false;
            for (RecordedCube cube : this.recordedCubes)
            {
                if (cube.mirrored != mirror)
                    continue;
                builder.append(".uv(").append(cube.texX).append(", ").append(cube.texY).append(")");
                if (mirror && !writtenMirror)
                {
                    builder.append(".mirrored()");
                    writtenMirror = true;
                }
                builder.append(".cuboid(").append(cube.minX).append("F, ").append(cube.minY).append("F, ").append(cube.minZ).append("F, ").append(cube.sizeX).append("F, ").append(cube.sizeY).append("F, ").append(cube.sizeZ).append('F');
                if (cube.xDilation == cube.yDilation && cube.yDilation == cube.zDilation)
                {
                    if (cube.xDilation != 0)
                    {
                        builder.append(", dilation.add(").append(cube.xDilation).append("F)");
                    }
                }
                else
                {
                    builder.append(", dilation.add(").append(cube.xDilation).append("F, ").append(cube.yDilation).append("F, ").append(cube.zDilation).append("F)");
                }
                builder.append(')');
            }
        }
    }

    public List<ModelConverterPart> children()
    {
        return recordedChildren;
    }

    public static class RecordedCube
    {
        public final int texX;
        public final int texY;
        public final float minX;
        public final float minY;
        public final float minZ;
        public final float sizeX;
        public final float sizeY;
        public final float sizeZ;
        public final float xDilation;
        public final float yDilation;
        public final float zDilation;
        public final boolean mirrored;

        public RecordedCube(int texX, int texY, float minX, float minY, float minZ, float sizeX, float sizeY, float sizeZ, float n, float o, float p, boolean mirrored)
        {
            this.texX = texX;
            this.texY = texY;
            this.minX = minX;
            this.minY = minY;
            this.minZ = minZ;
            this.sizeX = sizeX;
            this.sizeY = sizeY;
            this.sizeZ = sizeZ;
            this.xDilation = n;
            this.yDilation = o;
            this.zDilation = p;
            this.mirrored = mirrored;
        }
    }
}
