package com.ballz.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

/**
 * Looks up a public Minecraft username via Mojang's public API and shows the
 * skin link in chat. Client-side only: your real skin is not changed.
 */
public final class SkinLookup {
    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private SkinLookup() {}

    public static void lookup(String name) {
        msg("Looking up " + name + "...");

        Thread worker = new Thread(() -> {
            try {
                String uuid = fetchUuid(name);
                if (uuid == null) {
                    msg("No Minecraft account named " + name + ".");
                    return;
                }

                String skinUrl = fetchSkinUrl(uuid);
                if (skinUrl == null) {
                    msg(name + " is using the default skin.");
                    return;
                }

                msg(name + "'s skin: " + skinUrl);
            } catch (Exception e) {
                msg("Lookup failed: " + e.getMessage());
            }
        }, "ballz-skin-lookup");

        worker.setDaemon(true);
        worker.start();
    }

    private static String fetchUuid(String name) throws Exception {
        HttpResponse<String> res = send("https://api.mojang.com/users/profiles/minecraft/" + name);
        if (res.statusCode() != 200) return null;
        return JsonParser.parseString(res.body()).getAsJsonObject().get("id").getAsString();
    }

    private static String fetchSkinUrl(String uuid) throws Exception {
        HttpResponse<String> res = send("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid);
        if (res.statusCode() != 200) return null;

        JsonObject profile = JsonParser.parseString(res.body()).getAsJsonObject();
        for (JsonElement el : profile.getAsJsonArray("properties")) {
            JsonObject prop = el.getAsJsonObject();
            if (!"textures".equals(prop.get("name").getAsString())) continue;

            String json = new String(
                    Base64.getDecoder().decode(prop.get("value").getAsString()),
                    StandardCharsets.UTF_8);
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            if (!root.has("textures")) return null;

            JsonObject textures = root.getAsJsonObject("textures");
            if (!textures.has("SKIN")) return null;
            return textures.getAsJsonObject("SKIN").get("url").getAsString();
        }
        return null;
    }

    private static HttpResponse<String> send(String url) throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(8))
                .GET()
                .build();
        return HTTP.send(req, HttpResponse.BodyHandlers.ofString());
    }

    private static void msg(String text) {
        Minecraft mc = Minecraft.getInstance();
        mc.execute(() -> {
            if (mc.player != null) {
                mc.player.sendSystemMessage(Component.literal("[Ballz] " + text));
            }
        });
    }
}
