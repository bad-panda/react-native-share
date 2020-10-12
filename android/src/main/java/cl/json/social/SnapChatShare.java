package cl.json.social;

import android.content.ActivityNotFoundException;
import java.io.File;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;
import com.snapchat.kit.sdk.SnapCreative;
import com.snapchat.kit.sdk.creative.api.SnapCreativeKitApi;
import com.snapchat.kit.sdk.creative.exceptions.SnapMediaSizeException;
import com.snapchat.kit.sdk.creative.exceptions.SnapStickerSizeException;
import com.snapchat.kit.sdk.creative.exceptions.SnapVideoLengthException;
import com.snapchat.kit.sdk.creative.media.SnapMediaFactory;
import com.snapchat.kit.sdk.creative.media.SnapSticker;
import com.snapchat.kit.sdk.creative.media.SnapVideoFile;
import com.snapchat.kit.sdk.creative.models.SnapVideoContent;

/**
 * Created by Muhzi4u on 14-01-19.
 */
public class SnapChatShare extends SingleShareIntent {

    private static final String PACKAGE = "com.snapchat.android";
    private static final String CLASS = "com.snapchat.android.LandingPageActivity";
    private static final String PLAY_STORE_LINK = "market://details?id=com.snapchat.android";

    private SnapCreativeKitApi snapCreativeKitApi;
    private SnapMediaFactory snapMediaFactory;

    public SnapChatShare(ReactApplicationContext reactContext) {
        super(reactContext);

        snapCreativeKitApi = SnapCreative.getApi(reactContext);
        snapMediaFactory = SnapCreative.getMediaFactory(reactContext);
    }

    @Override
    public void open(ReadableMap options) throws ActivityNotFoundException {
        super.open(options);
        //  extra params here

        String type = options.getString("type");
        if ("video/mp4".equals(type)) {
            SnapVideoFile videoFile;
            try {
                String videoUrl = options.getString("url");
                File file = new File(videoUrl);
                videoFile = snapMediaFactory.getSnapVideoFromFile(file);
            } catch (SnapMediaSizeException | SnapVideoLengthException e) {
                return;
            }

            SnapVideoContent snapVideoContent = new SnapVideoContent(videoFile);

            // we use title instead of message because it will get appended to url
            if (options.hasKey("title")) {
                snapVideoContent.setCaptionText(options.getString("title"));
            }

            if (options.hasKey("attachmentUrl")) {
                snapVideoContent.setAttachmentUrl(options.getString("attachmentUrl"));
            }

            if (options.hasKey("sticker")) {
                SnapSticker snapSticker;
                try {
                    File stickerFile = new File(options.getString("sticker"));
                    snapSticker = snapMediaFactory.getSnapStickerFromFile(stickerFile);
                    snapSticker.setPosX(0.5f);
                    snapSticker.setPosY(0.5f);
                    snapSticker.setRotationDegreesClockwise(3.14f);
                    snapVideoContent.setSnapSticker(snapSticker);
                } catch (SnapStickerSizeException e) {
                    return;
                }
            }

            snapCreativeKitApi.send(snapVideoContent);
        }
    }

    @Override
    protected String getPackage() {
        return PACKAGE;
    }

    @Override
    protected String getComponentClass() { return CLASS; }

    @Override
    protected String getDefaultWebLink() {
        return null;
    }

    @Override
    protected String getPlayStoreLink() {
        return PLAY_STORE_LINK;
    }
}
