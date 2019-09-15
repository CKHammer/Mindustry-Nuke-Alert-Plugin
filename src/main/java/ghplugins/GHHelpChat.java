package ghplugins;

import io.anuke.arc.collection.Array;
import io.anuke.arc.util.CommandHandler;
import io.anuke.arc.util.Strings;
import io.anuke.mindustry.core.NetClient;
import io.anuke.mindustry.entities.type.Player;
import io.anuke.mindustry.plugin.Plugin;

import static io.anuke.mindustry.Vars.*;

public class GHHelpChat extends Plugin {

    @Override
    public void registerClientCommands(CommandHandler handler) {
        handler.<Player>register("hc", "<message...>", "Help Chat", (arg, player) -> process(arg[0], player));
    }

    /*
    * If the arg is "list', then return the player ID list
    * If Player is an Admin, then if he mentioned someone, that someone will receive that message.
    * If Player is NOT an Admin, then he cannot mention anyone and can only send message to Admins.
    */
    private void process(String arg, Player player){
        String[] args = arg.split(" ");
        if(args[0].equals("list") && player.isAdmin){
            int index = 0;
            if(Strings.canParseInt(args[1]))
                index = Strings.parseInt(args[1]);
            Array<String> list = new Array<>();
            playerGroup.all().each(p -> list.add("[green]< " + NetClient.colorizeName(p.id, p.name) + " [green]>: [accent]#[green]" + p.id));

            player.sendMessage(new Array<>(true, list.toArray(), index, 5).toString(",\n"));
            return;
        }

        Array<Player> players = playerGroup.all();
        String message = arg;
        if(player.isAdmin) {
            Player found;
            if (args[0].length() > 1 && args[0].startsWith("#") && Strings.canParseInt(args[0].substring(1)))
                found = playerGroup.find(p -> p.con != null && p.id == Strings.parseInt(args[0].substring(1)));
            else
                found = playerGroup.find(p -> p.name.equalsIgnoreCase(args[0]));
            if (found != null) {
                players.add(found);
                args[0] = "[gold]@" + NetClient.colorizeName(found.id, found.name);
                StringBuilder sb = new StringBuilder();
                for(String str : args)
                    sb.append(str).append(" ");
                message = sb.toString();
            }
        }
        final String msg = message;
        players.each(p -> p.isAdmin, o -> o.sendMessage(msg, player, "[green]<HC>" + NetClient.colorizeName(player.id, player.name)));
    }
}
