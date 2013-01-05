package hx.Lockit;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class BlockHangedLockRenderer implements ISimpleBlockRenderingHandler
{
    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID,
            RenderBlocks renderer)
    {
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
            Block block, int modelId, RenderBlocks renderer)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean shouldRender3DInInventory()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getRenderId()
    {
        // TODO Auto-generated method stub
        return ModLockit.instance.block("HangedLock").ri();
    }
}
