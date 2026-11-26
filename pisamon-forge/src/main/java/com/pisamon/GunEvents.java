package com.pisamon;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * ดักดาเมจจากปืน TaCZ
 * หมายเหตุ: ชื่อ method ของ event (getHurtEntity/getAttacker/getAmmoId) ให้ตรวจกับ TaCZ เวอร์ชันที่ใช้
 */
public class GunEvents {
    @SubscribeEvent
    public void onGunHurt(EntityHurtByGunEvent.Pre e) {
        if (!(e.getHurtEntity() instanceof BlackGhost ghost)) return;
        if (ghost.level().isClientSide) return;

        if (MagicAmmo.isMagic(e.getAmmoId())) {
            if (ghost.tryDodge(e.getAttacker())) { e.setCanceled(true); return; } // ผีวาปหลบ
            ghost.openMagicWindow();
            ghost.sparks();
            ghost.wail();
        } else {
            ghost.taunt(e.getAttacker());
            e.setCanceled(true); // กระสุนธรรมดา ไม่โดน
        }
    }
}
