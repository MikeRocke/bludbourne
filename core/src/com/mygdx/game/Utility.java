package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.mygdx.game.util.StringUtils;

public class Utility {
    public static final AssetManager ASSET_MANAGER = new AssetManager();
    private static final String TAG = Utility.class.getSimpleName();
    private static final InternalFileHandleResolver FILE_HANDLE_RESOLVER = new InternalFileHandleResolver();

    public static void unloadAsset(String assetFilenamePath) {
        if (isAssetLoaded(assetFilenamePath)) {
            ASSET_MANAGER.unload(assetFilenamePath);
        }
    }

    public static float loadCompleted() {
        return ASSET_MANAGER.getProgress();
    }

    public static int numberAssetsQueued() {
        return ASSET_MANAGER.getQueuedAssets();
    }

    public static boolean updateAssetLoading() {
        return ASSET_MANAGER.update();
    }

    public static  boolean isAssetLoaded(String filename) {
        return ASSET_MANAGER.isLoaded(filename);
    }

    public static void loadMapAsset(String mapFilename) {
        if (!StringUtils.isEmpty(mapFilename) && FILE_HANDLE_RESOLVER.resolve(mapFilename).exists()) {
            Class<TiledMap> type = TiledMap.class;
            TmxMapLoader mapLoader = new TmxMapLoader(FILE_HANDLE_RESOLVER);
            ASSET_MANAGER.setLoader(type, mapLoader);
            ASSET_MANAGER.load(mapFilename, type);
            ASSET_MANAGER.finishLoadingAsset(mapFilename);
        }
    }

    public static TiledMap getMapAsset(String mapFilename) {
        if (ASSET_MANAGER.isLoaded(mapFilename)) {
            return ASSET_MANAGER.get(mapFilename, TiledMap.class);
        } else {
            Gdx.app.debug(TAG, "Map is not loaded: " + mapFilename);
            return null;
        }
    }

    public static void loadTextureAsset(String filename) {
        if (!StringUtils.isEmpty(filename) && FILE_HANDLE_RESOLVER.resolve(filename).exists()) {
            Class<Texture> type = Texture.class;
            TextureLoader mapLoader = new TextureLoader(FILE_HANDLE_RESOLVER);
            ASSET_MANAGER.setLoader(type, mapLoader);
            ASSET_MANAGER.load(filename, type);
            ASSET_MANAGER.finishLoadingAsset(filename);
        }
    }

    public static Texture getTextureAsset(String filename) {
        if (ASSET_MANAGER.isLoaded(filename)) {
            return ASSET_MANAGER.get(filename, Texture.class);
        } else {
            Gdx.app.debug(TAG, "Texture is not loaded: " + filename);
            return null;
        }
    }

}
