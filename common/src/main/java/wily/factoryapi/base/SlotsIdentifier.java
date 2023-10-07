package wily.factoryapi.base;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.awt.*;


public record SlotsIdentifier(ChatFormatting color, String name,Component component) {
    public static SlotsIdentifier WATER = new SlotsIdentifier(ChatFormatting.BLUE,"water");
    public static SlotsIdentifier LAVA= new SlotsIdentifier(ChatFormatting.GOLD,"lava");
    public static SlotsIdentifier INPUT = new SlotsIdentifier(ChatFormatting.BLUE,"input");
    public static SlotsIdentifier OUTPUT = new SlotsIdentifier(ChatFormatting.RED,"output");
    public static SlotsIdentifier FUEL = new SlotsIdentifier(ChatFormatting.GOLD,"fuel");
    public static SlotsIdentifier PURPLE =  new SlotsIdentifier(ChatFormatting.DARK_PURPLE,"input_output");
    public static SlotsIdentifier ENERGY =  new SlotsIdentifier(ChatFormatting.AQUA,"energy");
    public static SlotsIdentifier GENERIC = new SlotsIdentifier(ChatFormatting.GRAY,"single");

    public Color getColor(){
        return new Color(color.getColor());
    }

    public SlotsIdentifier(ChatFormatting color, String name){
        this(color,name,Component.translatable("tooltip.factory_api.config.identifier." + name));
    }

    public String getFormattedName(){
        return component.getString();
    }
    public Component getTooltip(String type){
        return Component.translatable("tooltip.factory_api.config." + type, getFormattedName());
    }
    public String getName() {
        return this.name();

    }
}
