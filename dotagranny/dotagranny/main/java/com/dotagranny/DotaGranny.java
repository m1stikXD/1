package com.dotagranny;

import com.dotagranny.entity.GrannyEntity;
import com.dotagranny.item.AegisItem;
import com.dotagranny.item.BlinkDaggerItem;
import com.dotagranny.item.HookItem;
import com.dotagranny.item.SalveItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(DotaGranny.MODID)
public class DotaGranny {
    public static final String MODID = "dotagranny";

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Моб
    public static final RegistryObject<EntityType<GrannyEntity>> GRANNY = ENTITIES.register("granny",
            () -> EntityType.Builder.of(GrannyEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8)
                    .build(MODID + ":granny"));

    // Предметы
    public static final RegistryObject<Item> GOLD_COIN = ITEMS.register("gold_coin",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BLINK_DAGGER = ITEMS.register("blink_dagger",
            () -> new BlinkDaggerItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> HOOK = ITEMS.register("hook",
            () -> new HookItem(new Item.Properties().durability(64)));
    public static final RegistryObject<Item> SALVE = ITEMS.register("salve",
            () -> new SalveItem(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> AEGIS = ITEMS.register("aegis",
            () -> new AegisItem(new Item.Properties().stacksTo(1).rarity(net.minecraft.world.item.Rarity.EPIC)));
    public static final RegistryObject<Item> GRANNY_SPAWN_EGG = ITEMS.register("granny_spawn_egg",
            () -> new ForgeSpawnEggItem(GRANNY, 0x6E3C8C, 0xC8C8C8, new Item.Properties()));

    // Вкладка креатива
    public static final RegistryObject<CreativeModeTab> MAIN_TAB = TABS.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.dotagranny"))
                    .icon(() -> new ItemStack(BLINK_DAGGER.get()))
                    .displayItems((params, out) -> ITEMS.getEntries().forEach(e -> out.accept(e.get())))
                    .build());

    public DotaGranny() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);
        ENTITIES.register(bus);
        TABS.register(bus);
    }
}
