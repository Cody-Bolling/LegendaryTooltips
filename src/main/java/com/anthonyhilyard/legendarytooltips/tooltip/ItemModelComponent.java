package com.anthonyhilyard.legendarytooltips.tooltip;

import java.util.List;

import com.anthonyhilyard.iceberg.renderer.CustomItemRenderer;
import com.anthonyhilyard.iceberg.util.GuiHelper;
import com.anthonyhilyard.iceberg.util.Tooltips.InlineComponent;
import com.anthonyhilyard.prism.text.DynamicColor;
import com.anthonyhilyard.prism.util.ColorUtil;
import com.anthonyhilyard.prism.util.ConfigHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ItemModelComponent implements TooltipComponent, ClientTooltipComponent, InlineComponent
{
	private static CustomItemRenderer customItemRenderer = null;

	private static float rotationTimer = 0.0f;

	private final ItemStack itemStack;

	public static void updateTimer(float partialTick)
	{
		rotationTimer += partialTick;
		if (rotationTimer > 12.0f)
		{
			rotationTimer -= 12.0f;
		}
	}

	public ItemModelComponent(ItemStack itemStack)
	{
		this.itemStack = itemStack;

		if (customItemRenderer == null)
		{
			Minecraft minecraft = Minecraft.getInstance();
			customItemRenderer = new CustomItemRenderer(minecraft.getTextureManager(), minecraft.getModelManager(), minecraft.getItemColors(), minecraft.getItemRenderer().getBlockEntityRenderer(), minecraft);
		}
	}

	public int getRenderHeight() { return 22; }
	public int getRenderWidth() { return 22; }
	
	@Override
	public int getHeight() { return 4; }

	@Override
	public int getWidth(Font p_169952_) { return getRenderWidth(); }

	@Override
	public void renderImage(Font font, int x, int y, PoseStack poseStack, ItemRenderer itemRenderer, int z)
	{
		y--;
		x--;
		final int margin = 2;

		DynamicColor borderStartColor = DynamicColor.fromRgb(TooltipDecor.currentTooltipBorderStart);
		DynamicColor backgroundStartColor = DynamicColor.fromRgb(TooltipDecor.currentTooltipBackgroundStart);
		DynamicColor backgroundEndColor = ConfigHelper.applyModifiers(List.of("v+35", "s+10"), DynamicColor.fromRgb(TooltipDecor.currentTooltipBackgroundEnd));

		int borderStart = ColorUtil.combineARGB((int)(borderStartColor.alpha() * 0.35f), borderStartColor.red(), borderStartColor.green(), borderStartColor.blue());
		int backgroundStart = ColorUtil.combineARGB((int)(backgroundStartColor.alpha() * 0.15f), backgroundStartColor.red(), backgroundStartColor.green(), backgroundStartColor.blue());
		int backgroundEnd = ColorUtil.combineARGB((int)(backgroundEndColor.alpha() * 0.6f), backgroundEndColor.red(), backgroundEndColor.green(), backgroundEndColor.blue());

		// Draw the background first.
		GuiHelper.drawGradientRect(poseStack.last().pose(), z, x + margin + 1, y + margin + 1, x + getRenderWidth() - margin - 1, y + getRenderHeight() - margin - 1, backgroundStart, backgroundEnd);
		GuiHelper.drawGradientRect(poseStack.last().pose(), z, x + margin + 1, y + margin + 1, x + getRenderWidth() - margin - 1, y + getRenderHeight() - margin - 1, backgroundEnd, backgroundStart);
		GuiHelper.drawGradientRectHorizontal(poseStack.last().pose(), z, x + margin + 1, y + margin + 1, x + getRenderWidth() - margin - 1, y + getRenderHeight() - margin - 1, backgroundStart, backgroundEnd);
		GuiHelper.drawGradientRectHorizontal(poseStack.last().pose(), z, x + margin + 1, y + margin + 1, x + getRenderWidth() - margin - 1, y + getRenderHeight() - margin - 1, backgroundEnd, backgroundStart);
		
		// Draw the border.
		GuiHelper.drawGradientRect(poseStack.last().pose(), z, x + margin + 1, y + margin, x + getRenderWidth() - margin - 1, y + margin + 1, borderStart, borderStart);
		GuiHelper.drawGradientRect(poseStack.last().pose(), z, x + margin + 1, y + getRenderHeight() - margin - 1, x + getRenderWidth() - margin - 1, y + getRenderHeight() - margin, borderStart, borderStart);
		GuiHelper.drawGradientRect(poseStack.last().pose(), z, x + margin, y + margin + 1, x + margin + 1, y + getRenderHeight() - margin - 1, borderStart, borderStart);
		GuiHelper.drawGradientRect(poseStack.last().pose(), z, x + getRenderWidth() - margin - 1, y + margin + 1, x + getRenderWidth() - margin, y + getRenderHeight() - margin - 1, borderStart, borderStart);

		final PoseStack modelViewStack = RenderSystem.getModelViewStack();
		modelViewStack.pushPose();
		modelViewStack.translate(x + margin - 1, y + margin - 1, 400.0f);
		modelViewStack.scale(1.25f, 1.25f, 1.0f);
		RenderSystem.applyModelViewMatrix();

		customItemRenderer.renderDetailModelIntoGUI(itemStack, 0, 0, Vector3f.YP.rotationDegrees(Mth.lerp(rotationTimer / 12.0f, 0, 360.0f)));

		modelViewStack.popPose();
		RenderSystem.applyModelViewMatrix();
	}

	public static void registerFactory()
	{
		FMLJavaModLoadingContext.get().getModEventBus().addListener(ItemModelComponent::onRegisterTooltipEvent);
	}

	private static void onRegisterTooltipEvent(RegisterClientTooltipComponentFactoriesEvent event)
	{
		event.register(ItemModelComponent.class, x -> x);
	}
}
