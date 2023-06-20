package wily.factoryapi.base;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import java.awt.*;


public class SlotsIdentifier {
    public static SlotsIdentifier WATER = new SlotsIdentifier(ChatFormatting.BLUE,"water",0);
    public static SlotsIdentifier LAVA= new SlotsIdentifier(ChatFormatting.GOLD,"lava",1);
    public static SlotsIdentifier INPUT = new SlotsIdentifier(ChatFormatting.BLUE,"input",0);
    public static SlotsIdentifier OUTPUT = new SlotsIdentifier(ChatFormatting.RED,"output",1);
    public static SlotsIdentifier FUEL = new SlotsIdentifier(ChatFormatting.GOLD,"fuel",2);
    public static SlotsIdentifier PURPLE =  new SlotsIdentifier(ChatFormatting.DARK_PURPLE,"input_output",3);
    public static SlotsIdentifier ENERGY =  new SlotsIdentifier(ChatFormatting.AQUA,"energy",4);
    public static SlotsIdentifier GENERIC = new SlotsIdentifier(ChatFormatting.GRAY,"single",0);

    public final ChatFormatting color;
    public final String name;
    public final int differential;
    public SlotsIdentifier(ChatFormatting color, String name,int differential){
        this.color = color;
        this.name = name;
        this.differential = differential;
    }

    public Color getColor(){
        return new Color(color.getColor());
    }

    public String getFormattedName(){
        return new TranslatableComponent("tooltip.factory_api.config.identifier." + getName()).getString();
    }
    public Component getTooltip(String type){
        return new TranslatableComponent("tooltip.factory_api.config." + type, getFormattedName());
    }
    public String getName() {
        return this.name;

    }
}
