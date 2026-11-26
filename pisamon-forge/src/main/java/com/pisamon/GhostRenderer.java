package com.pisamon;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;

public class GhostRenderer extends HumanoidMobRenderer<BlackGhost, HumanoidModel<BlackGhost>> {
    private static final ResourceLocation TEX = new ResourceLocation(PisamonMod.MODID, "textures/entity/black_ghost.png");

    public GhostRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.ZOMBIE)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(BlackGhost e) { return TEX; }

    @Override
    protected void scale(BlackGhost e, PoseStack ps, float pt) { ps.scale(1.15F, 1.3F, 1.15F); }
}
