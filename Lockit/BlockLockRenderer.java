package hx.Lockit;

import hx.utils.BlockLoader;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class BlockLockRenderer implements ISimpleBlockRenderingHandler
{
    private static float OFF = 0.05f;
    private static float YOFF = 0.04f;
    private static float WOFF = 0.1f;

    private BlockLoader bl;
    private int ri = -1;
    
    private float[] getStickVert(ForgeDirection dir)
    {
        float[] vert = new float[6];
        //{0,0,0,dir.offsetX,dir.offsetY,dir.offsetZ};
        vert[0] = Math.min((dir.offsetX / 2f) - OFF, dir.offsetX * 0.75f - OFF) + 0.5f;
        vert[1] = Math.min((dir.offsetY / 2f) - YOFF, dir.offsetY * 0.75f - YOFF) + 0.5f;
        vert[2] = Math.min((dir.offsetZ / 2f) - OFF, dir.offsetZ * 0.75f - OFF) + 0.5f;
        vert[3] = Math.max((dir.offsetX / 2f) + OFF, dir.offsetX * 0.75f + OFF) + 0.5f;
        vert[4] = Math.max((dir.offsetY / 2f) + YOFF, dir.offsetY * 0.75f + YOFF) + 0.5f;
        vert[5] = Math.max((dir.offsetZ / 2f) + OFF, dir.offsetZ * 0.75f + OFF) + 0.5f;
        return vert;
    }

    private float[] getHandleVert(ForgeDirection dir)
    {
        float[] vert = new float[6];
        //{0,0,0,dir.offsetX,dir.offsetY,dir.offsetZ};
        vert[0] = dir.offsetX * 0.75f - WOFF + 0.5f;
        vert[1] = dir.offsetY * 0.75f - OFF + 0.5f;
        vert[2] = dir.offsetZ * 0.75f - WOFF + 0.5f;
        vert[3] = dir.offsetX * 0.75f + WOFF + 0.5f;
        vert[4] = dir.offsetY * 0.75f + OFF + 0.5f;
        vert[5] = dir.offsetZ * 0.75f + WOFF + 0.5f;
        return vert;
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID,
            RenderBlocks renderer)
    {
    	if(bl == null)bl = ModLockit.instance.block("Lock");
    	if(ri == -1)ri = bl.ri();
    	 
    	bl.blockRI = 0;
    	renderer.renderBlockAsItem(block,0xffffff,1f);
    	bl.blockRI = ri;
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
            Block block, int modelId, RenderBlocks renderer)
    {
        renderer.renderStandardBlock(block, x, y, z);
        int face = world.getBlockMetadata(x, y, z);

        if (face >= 6)
        {
            return false;
        }

        ForgeDirection dir = ForgeDirection.getOrientation(face);
        boolean renderAll = renderer.renderAllFaces;
        renderer.renderAllFaces = true;
        float[] vert = getStickVert(dir);
        renderer.setRenderMinMax(vert[0], vert[1], vert[2], vert[3], vert[4], vert[5]);
        renderer.renderStandardBlock(block, x, y, z);
        vert = getHandleVert(dir);
        renderer.setRenderMinMax(vert[0], vert[1], vert[2], vert[3], vert[4], vert[5]);
        renderer.renderStandardBlock(block, x, y, z);
        renderer.renderAllFaces = renderAll;
        return false;
    }

    @Override
    public boolean shouldRender3DInInventory()
    {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public int getRenderId()
    {
        // TODO Auto-generated method stub
        return ModLockit.instance.block("Lock").ri();
    }
}
