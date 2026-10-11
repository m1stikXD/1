package com.dotagranny.client;

import com.dotagranny.DotaGranny;
import com.dotagranny.entity.GrannyEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class GrannyRenderer extends HumanoidMobRenderer<GrannyEntity, HumanoidModel<GrannyEntity>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(DotaGranny.MODID, "textures/entity/granny.png");

    public GrannyRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<GrannyEntity>(context.bakeLayer(ModelLayers.ZOMBIE)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(GrannyEntity entity) {
        return TEXTURE;
    }
}
