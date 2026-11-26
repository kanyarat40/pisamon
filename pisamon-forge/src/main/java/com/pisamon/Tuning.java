package com.pisamon;

/** ปรับความโหด/ความเยอะของผีได้ที่ไฟล์นี้ที่เดียว */
public final class Tuning {
    private Tuning() {}

    // ---- เลือด ----
    public static final double GHOST_HP = 200.0;   // เดิม 60 (กระสุนอาคมเลยต้องยิงเยอะขึ้น)
    public static final double TIGER_HP = 260.0;

    // ---- ผีเกิดในป่า (ต่อผู้เล่น 1 คน) ----
    public static final int    NEARBY_RADIUS      = 48;   // นับผีรอบตัวผู้เล่นในรัศมีนี้
    public static final int    NIGHT_CAP_MIN      = 6;    // เพดานผีตอนหัวค่ำ/ใกล้สว่าง
    public static final int    NIGHT_CAP_MAX      = 16;   // เพดานผีตอนดึกสุด (เที่ยงคืน)
    public static final double NIGHT_CHANCE       = 0.65; // โอกาสเกิดต่อรอบ (ทุก 2 วิ) ตอนกลางคืน
    public static final double DAY_CHANCE         = 0.006;// ตอนกลางวัน แทบไม่เจอ (ต้องอยู่ใต้ร่มไม้ทึบด้วย)
    public static final int    DAY_CAP            = 1;
    public static final int    PACK_MAX           = 3;    // เกิดเป็นฝูงสูงสุดกี่ตัวต่อครั้ง (ตอนกลางคืน)
    public static final double RAIN_BONUS         = 1.4;  // ฝนตก/ฟ้าร้อง ผีเยอะขึ้น
    public static final double TIGER_SHARE        = 0.07; // สัดส่วนที่จะเป็นเสือสมิง (มีได้ทีละตัวรอบผู้เล่น)
    public static final int    SPAWN_MIN_DIST     = 20;
    public static final int    SPAWN_MAX_DIST     = 38;
}
