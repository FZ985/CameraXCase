package camerax.usecase;

/**
 * author : JFZ
 * date : 2023/7/18 14:17
 * description :
 */
public class EventCode {

    public static final int EVENT_TAKE_PICTURE = "TakePictureEvent".hashCode();

    public static final int EVENT_TAKE_PICTURE_ERROR = "TakePictureErrorEvent".hashCode();

    public static final int EVENT_SWITCH_BACK = "LensFacingBackEvent".hashCode();

    public static final int EVENT_SWITCH_FRONT = "LensFacingFrontEvent".hashCode();

    public static final int EVENT_FLASH_MODE_AUTO = "FlashModeAutoEvent".hashCode();

    public static final int EVENT_FLASH_MODE_ON = "FlashModeOnEvent".hashCode();

    public static final int EVENT_FLASH_MODE_OFF = "FlashModeOffEvent".hashCode();
}
