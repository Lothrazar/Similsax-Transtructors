package com.vorquel.similsaxtranstructors;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BlockOverlay {

  private final ResourceLocation overlayLocation = new ResourceLocation(SimilsaxTranstructors.MODID.toLowerCase(), "textures/overlay.png");
  private final Vec3d[] vs = new Vec3d[8];
  {
    for (int i = 0; i < 8; ++i) {
      int x = (i & 1) == 1 ? 1 : 0;
      int y = (i & 2) == 2 ? 1 : 0;
      int z = (i & 4) == 4 ? 1 : 0;
      vs[i] = new Vec3d(x, y, z);
    }
  }
  int arrow1 = 0;
  int arrow2 = 1;
  int arrow3 = 2;
  int arrow4 = 3;
  int cross = 4;
  int bullseye = 5;
  int cancel = 6;
  private final float[][][] uvs = new float[7][4][];
  {
    //ararrow1ow 1
    uvs[arrow1][0] = new float[] { 0, 0 };
    uvs[arrow1][1] = new float[] { 0, .5f };
    uvs[arrow1][2] = new float[] { .5f, .5f };
    uvs[arrow1][3] = new float[] { .5f, 0 };
    //arrow 2
    uvs[arrow2][0] = new float[] { 0, .5f };
    uvs[arrow2][1] = new float[] { .5f, .5f };
    uvs[arrow2][2] = new float[] { .5f, 0 };
    uvs[arrow2][3] = new float[] { 0, 0 };
    //arrow 3
    uvs[arrow3][0] = new float[] { .5f, .5f };
    uvs[arrow3][1] = new float[] { .5f, 0 };
    uvs[arrow3][2] = new float[] { 0, 0 };
    uvs[arrow3][3] = new float[] { 0, .5f };
    //arrow 4
    uvs[arrow4][0] = new float[] { .5f, 0 };
    uvs[arrow4][1] = new float[] { 0, 0 };
    uvs[arrow4][2] = new float[] { 0, .5f };
    uvs[arrow4][3] = new float[] { .5f, .5f };
    //cross
    uvs[cross][0] = new float[] { .5f, 0 };
    uvs[cross][1] = new float[] { .5f, .5f };
    uvs[cross][2] = new float[] { 1, .5f };
    uvs[cross][3] = new float[] { 1, 0 };
    //bullseye
    uvs[bullseye][0] = new float[] { 0, .5f };
    uvs[bullseye][1] = new float[] { 0, 1 };
    uvs[bullseye][2] = new float[] { .5f, 1 };
    uvs[bullseye][3] = new float[] { .5f, .5f };
    //cancel
    uvs[cancel][0] = new float[] { .5f, .5f };
    uvs[cancel][1] = new float[] { .5f, 1 };
    uvs[cancel][2] = new float[] { 1, 1 };
    uvs[cancel][3] = new float[] { 1, .5f };
  }

  @SubscribeEvent
  public void renderOverlay(DrawBlockHighlightEvent event) {
    if (shouldSkip(event)) {
      return;
    }
    RayTraceResult m = event.getTarget();
    if (m.getType() == RayTraceResult.Type.BLOCK) {
      BlockRayTraceResult result = ((BlockRayTraceResult) m);
      BlockPos mPos = new BlockPos(m.getHitVec()); //m.getBlockPos();
      Vec3d hitVec = m.getHitVec();
      Direction indexd;
      int index = 6;
      int[] look = new int[6];
      if (isBadBlock(event)) {
        indexd = Direction.UP;
        index = 6;
        look = new int[] { cancel, cancel, cancel, cancel, cancel, cancel };
      }
      else {
        indexd = ItemSimilsax.getSide(result.getFace(), hitVec, mPos).getOpposite();
        index = indexd.ordinal();
        switch (indexd) {
          case DOWN:
            look = new int[] { arrow3, bullseye, arrow2, arrow2, cross, arrow3 };
          break;
          case UP:
            look = new int[] { arrow1, cross, arrow4, arrow4, bullseye, arrow1 };
          break;
          case NORTH:
            look = new int[] { arrow2, arrow3, bullseye, arrow3, arrow2, cross };
          break;
          case SOUTH:
            look = new int[] { arrow4, arrow1, cross, arrow1, arrow4, bullseye };
          break;
          case WEST:
            look = new int[] { bullseye, arrow2, arrow3, cross, arrow3, arrow2 };
          break;
          case EAST://5
            look = new int[] { cross, arrow4, arrow1, bullseye, arrow1, arrow4 };
          break;
          default:
          break;
        }
      }
      //      Minecraft.getInstance().eng
      //was renderEngine
      Minecraft.getInstance().textureManager.bindTexture(overlayLocation);
      Vec3d v = getViewerPosition(event.getPartialTicks());
      GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
      GL11.glPushMatrix();
      //      SimilsaxTranstructors.log.info("{} ::  mPos {} ({}  {}  {}) ", mPos, indexd, v.x, v.y, v.z);
      double xFix = 0;
      double yFix = 0.08;
      double zFix = 0;
      double yDiff = hitVec.y - mPos.getY();
      if (yDiff < ItemSimilsax.lo) {
        //low side
        yFix = -1.62;
        SimilsaxTranstructors.log.info("{} ::  LOW", indexd);
      }
      else if (yDiff > ItemSimilsax.hi) {
        yFix = -1.62;
        SimilsaxTranstructors.log.info("{} ::  HIGH", indexd);
      }
      GL11.glTranslated(mPos.getX() + xFix, mPos.getY() + yFix, mPos.getZ() + zFix);
      GL11.glTranslated(-v.x, -v.y, -v.z);
      GL11.glEnable(GL11.GL_ALPHA_TEST);
      GL11.glAlphaFunc(GL11.GL_GREATER, 0);
      GL11.glEnable(GL11.GL_BLEND);
      GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
      GL11.glColor4f(1, 1, 1, .375f);
      //P/N ONLY exist to prevent layer fighting/flashing, push it just outside ontop of the block, so 1 + this fract      
      final float P = 1 / 256f, N = -1 / 256f;
      final int X = 1, Y = 2, Z = 4;
      if (index >= 2 && index <= 5) {
        GL11.glTranslatef(0, -1.7F, 0);//2345
      }
      int TOP = 1, EAST = 0, SOUTH = 2, WEST = 3, BOTTOM = 4, NORTH = 5;
      //draw east
      GL11.glTranslatef(P, 0, 0);
      drawSide(X, Y, Z, uvs[look[EAST]]);// this one has to be est or west side
      //draw top
      GL11.glTranslatef(N, P, 0);
      drawSide(Y, Z, X, uvs[look[TOP]]); // TOP
      //SOUTH
      GL11.glTranslatef(0, N, P);
      drawSide(Z, X, Y, uvs[look[SOUTH]]);
      //WEST
      GL11.glTranslatef(N, 0, N);
      drawSide(0, Z, Y, uvs[look[WEST]]);
      //BOTTOM
      GL11.glTranslatef(P, N, 0);
      drawSide(0, X, Z, uvs[look[BOTTOM]]);
      //NORTH
      GL11.glTranslatef(0, P, N);
      drawSide(0, Y, X, uvs[look[NORTH]]);
      GL11.glPopMatrix();
      GL11.glPopAttrib();
    }
  }

  private boolean shouldSkip(DrawBlockHighlightEvent event) {
    if (event.getTarget().getType() != RayTraceResult.Type.BLOCK) {
      return true;
    }
    PlayerEntity p = Minecraft.getInstance().player;
    ItemStack mainItemStack = p.getHeldItem(Hand.MAIN_HAND);
    Item mainItem = (mainItemStack.isEmpty()) ? null : mainItemStack.getItem();
    ItemStack offItemStack = p.getHeldItem(Hand.OFF_HAND);
    Item offItem = (offItemStack.isEmpty()) ? null : offItemStack.getItem();
    return !(mainItem instanceof ItemSimilsax || offItem instanceof ItemSimilsax);
  }

  private boolean isBadBlock(DrawBlockHighlightEvent event) {
    return false;
    //    BlockPos pos = event.getTarget().getBlockPos();
    //    World world = event.getPlayer().world;
    //    IBlockState state = world.getBlockState(pos);
    //    Block block = state.getBlock();
    //    return block.hasTileEntity(state) || block.isReplaceable(world, pos);
  }

  private Vec3d getViewerPosition(float partialTicks) {
    Entity viewer = Minecraft.getInstance().getRenderViewEntity();
    double x = partial(partialTicks, viewer.prevPosX, viewer.posX);
    double y = partial(partialTicks, viewer.prevPosY, viewer.posY);
    double z = partial(partialTicks, viewer.prevPosZ, viewer.posZ);
    return new Vec3d(x, y, z);
  }

  private double partial(float partialTicks, double prevPos, double pos) {
    return partialTicks == 1 ? pos : prevPos + partialTicks * (pos - prevPos);
  }

  private void drawSide(int c, int i, int j, float[][] uv) {
    Tessellator.getInstance().getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
    addVertex(uv[0][0], uv[0][1], c);
    addVertex(uv[1][0], uv[1][1], c + i);
    addVertex(uv[2][0], uv[2][1], c + i + j);
    addVertex(uv[3][0], uv[3][1], c + j);
    Tessellator.getInstance().draw();
  }

  private void addVertex(double u, double v, int i) {
    Tessellator.getInstance().getBuffer().pos(vs[i].x, vs[i].y, vs[i].z).tex(u, v).endVertex();
  }
}
