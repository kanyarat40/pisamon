package com.pisamon;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.OcelotModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/** ใช้โมเดล+สกินลายจุดของ ocelot (vanilla) ขยายเป็นเสือยักษ์ ไว้ก่อน ค่อยทำสกินลายเสือจริงทีหลังได้ */
public class TigerRenderer extends MobRenderer<TigerSpirit, OcelotModel<TigerSpirit>> {
    private static final ResourceLocation TEX = new ResourceLocation("textures/entity/cat/ocelot.png");

    public TigerRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new OcelotModel<>(ctx.bakeLayer(ModelLayers.OCELOT)), 0.9F);
    }

    @Override
    public ResourceLocation getTextureLocation(TigerSpirit e) { return TEX; }

    @Override
    protected void scale(TigerSpirit e, PoseStack ps, float pt) { ps.scale(2.2F, 2.2F, 2.2F); }
}
