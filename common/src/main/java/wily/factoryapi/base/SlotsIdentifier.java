package wily.factoryapi.base;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.awt.*;


public record SlotsIdentifier(ChatFormatting color, String name,int differential) {
    public static SlotsIdentifier WATER = new SlotsIdentifier(ChatFormatting.BLUE,"water",0);
    public static SlotsIdentifier LAVA= new SlotsIdentifier(ChatFormatting.GOLD,"lava",1);
    public static SlotsIdentifier BLUE = new SlotsIdentifier(ChatFormatting.BLUE,"input",0);
    public static SlotsIdentifier RED = new SlotsIdentifier(ChatFormatting.RED,"output",1);
    public static SlotsIdentifier ORANGE = new SlotsIdentifier(ChatFormatting.GOLD,"fuel",2);
    public static SlotsIdentifier PURPLE =  new SlotsIdentifier(ChatFormatting.DARK_PURPLE,"input_output",3);
    public static SlotsIdentifier AQUA =  new SlotsIdentifier(ChatFormatting.AQUA,"energy",4);
    public static SlotsIdentifier GENERIC = new SlotsIdentifier(ChatFormatting.GRAY,"gray",0);


    public Color getColor(){
        return new Color(color.getColor());
    }

    public String getFormattedName(){
        return Component.translatable("tooltip.factory_api.config.identifier." + getName()).getString();
    }
    public Component getTooltip(String type){
        return Component.translatable("tooltip.factory_api.config." + type, getFormattedName());
    }
    public String getName() {
        return this.name();

    }
}
