package io.github.ocelot.common;

import net.minecraft.block.Block;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>Contains simple, useful methods for creating a {@link VoxelShape} with provided {@link Direction}.</p>
 *
 * @author Ocelot
 * @see VoxelShape
 * @since 2.0.0
 */
public class VoxelShapeHelper
{
    private VoxelShapeHelper() {}

    /**
     * Creates a rotated shape from an {@link Direction.Axis}. Everything is based on the negative axes facing positive.
     *
     * @param x1   The min x for the shape
     * @param y1   The min y for the shape
     * @param z1   The min z for the shape
     * @param x2   The max x for the shape
     * @param y2   The max y for the shape
     * @param z2   The max z for the shape
     * @param axis The axis to rotate on
     * @return The rotated box shape
     */
    public static VoxelShape makeCuboidShape(double x1, double y1, double z1, double x2, double y2, double z2, Direction.Axis axis)
    {
        switch (axis)
        {
            case X:
                return makeCuboidShape(x1, y1, z1, x2, y2, z2, Direction.EAST);
            case Y:
                return makeCuboidShape(x1, y1, z1, x2, y2, z2, Direction.UP);
            case Z:
                return makeCuboidShape(x1, y1, z1, x2, y2, z2, Direction.SOUTH);
            default:
                throw new IllegalStateException("Unexpected value: " + axis);
        }
    }

    /**
     * Creates a rotated shape from a {@link Direction}. Everything is based on the negative axes facing positive (Ex. minX to maxX, minY to maxY, and minZ to maxZ).
     *
     * @param x1        The min x for the shape
     * @param y1        The min y for the shape
     * @param z1        The min z for the shape
     * @param x2        The max x for the shape
     * @param y2        The max y for the shape
     * @param z2        The max z for the shape
     * @param direction The direction to rotate towards
     * @return The rotated box shape
     */
    public static VoxelShape makeCuboidShape(double x1, double y1, double z1, double x2, double y2, double z2, Direction direction)
    {
        switch (direction)
        {
            case UP:
                return Block.makeCuboidShape(x1, y1, z1, x2, y2, z2);
            case DOWN:
                return Block.makeCuboidShape(x1, 16 - y1, z1, x2, 16 - y2, z2);
            case NORTH:
                return Block.makeCuboidShape(x1, z1, 16 - y1, x2, z2, 16 - y2);
            case EAST:
                return Block.makeCuboidShape(y1, z1, x1, y2, z2, x2);
            case SOUTH:
                return Block.makeCuboidShape(x1, z1, y1, x2, z2, y2);
            case WEST:
                return Block.makeCuboidShape(16 - y1, z1, x1, 16 - y2, z2, x2);
            default:
                throw new IllegalStateException("Unexpected value: " + direction);
        }
    }

    /**
     * <p>Manages the combining of {@link VoxelShape} into a single complex shape.</p>
     *
     * @author Ocelot
     * @see VoxelShape
     * @since 0.2.0
     */
    public static final class Builder
    {
        private final Set<VoxelShape> shapes;

        public Builder()
        {
            this.shapes = new HashSet<>();
        }

        /**
         * Appends the specified shapes to the sets.
         *
         * @param shapes The shapes to add
         * @return The builder instance for chaining
         */
        public Builder append(VoxelShape... shapes)
        {
            this.shapes.addAll(Arrays.asList(shapes));
            return this;
        }

        /**
         * Rotates the entire shape in the specified axis.
         *
         * @param axis The axis to rotate on
         * @return The rotated builder
         */
        public Builder rotate(Direction.Axis axis)
        {
            Builder newBuilder = new Builder();
            for (VoxelShape shape : this.shapes)
            {
                Set<VoxelShape> rotatedShapes = new HashSet<>();
                for (AxisAlignedBB box : shape.toBoundingBoxList())
                {
                    rotatedShapes.add(VoxelShapeHelper.makeCuboidShape(box.minX * 16.0, box.minY * 16.0, box.minZ * 16.0, box.maxX * 16.0, box.maxY * 16.0, box.maxZ * 16.0, axis));
                }
                VoxelShape result = VoxelShapes.empty();
                for (VoxelShape rotatedShape : rotatedShapes)
                {
                    result = VoxelShapes.combine(result, rotatedShape, IBooleanFunction.OR);
                }
                newBuilder.append(result);
            }
            return newBuilder;
        }

        /**
         * Rotates the entire shape in the specified direction.
         *
         * @param direction The direction to rotate on
         * @return The rotated builder
         */
        public Builder rotate(Direction direction)
        {
            Builder newBuilder = new Builder();
            for (VoxelShape shape : this.shapes)
            {
                Set<VoxelShape> rotatedShapes = new HashSet<>();
                for (AxisAlignedBB box : shape.toBoundingBoxList())
                {
                    rotatedShapes.add(VoxelShapeHelper.makeCuboidShape(box.minX * 16.0, box.minY * 16.0, box.minZ * 16.0, box.maxX * 16.0, box.maxY * 16.0, box.maxZ * 16.0, direction));
                }
                VoxelShape result = VoxelShapes.empty();
                for (VoxelShape rotatedShape : rotatedShapes)
                {
                    result = VoxelShapes.combine(result, rotatedShape, IBooleanFunction.OR);
                }
                newBuilder.append(result);
            }
            return newBuilder;
        }

        /**
         * @return A combined shape using {@link IBooleanFunction#OR}
         */
        public VoxelShape build()
        {
            return this.build(IBooleanFunction.OR);
        }

        /**
         * Combines the appended shapes into a single complex shape.
         *
         * @param combineFunction The function to use when combining the shapes together
         * @return A combined shape using the provided function
         */
        public VoxelShape build(IBooleanFunction combineFunction)
        {
            if (this.shapes.isEmpty())
                return VoxelShapes.empty();
            VoxelShape result = VoxelShapes.empty();
            for (VoxelShape shape : this.shapes)
            {
                result = VoxelShapes.combine(result, shape, combineFunction);
            }
            return result.simplify();
        }
    }
}
