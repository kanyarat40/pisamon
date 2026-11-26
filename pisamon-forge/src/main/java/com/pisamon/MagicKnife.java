package com.pisamon;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;

/** มีดอาคม: ทำดาเมจใส่ผีได้ */
public class MagicKnife extends SwordItem {
    public MagicKnife(Properties p) { super(Tiers.IRON, 2, -1.8F, p); }
    @Override public boolean isFoil(ItemStack s) { return true; }
}
