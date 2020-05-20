package io.github.ocelot.network;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

/**
 * @author Ocelot
 */
public class DisplayScreenMessage
{
    private GuiType type;
    private BlockPos pos;

    public DisplayScreenMessage(GuiType type, @Nullable BlockPos pos)
    {
        this.type = type;
        this.pos = pos;
    }

    public static void encode(DisplayScreenMessage msg, PacketBuffer buf)
    {
        buf.writeVarInt(msg.type.ordinal());
        buf.writeBoolean(msg.pos != null);
        if (msg.pos != null)
        {
            buf.writeBlockPos(msg.pos);
        }
    }

    public static DisplayScreenMessage decode(PacketBuffer buf)
    {
        return new DisplayScreenMessage(GuiType.values()[buf.readVarInt() % GuiType.values().length], buf.readBoolean() ? buf.readBlockPos() : null);
    }

    public GuiType getType()
    {
        return type;
    }

    @Nullable
    public BlockPos getPos()
    {
        return pos;
    }

    public enum GuiType
    {
        VALUE_CONTAINER_EDITOR
    }
}
