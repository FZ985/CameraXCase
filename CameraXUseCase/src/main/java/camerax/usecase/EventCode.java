package camerax.usecase;

/**
 * author : JFZ
 * date : 2023/7/18 14:17
 * description :
 */
public class EventCode {

    public static final int EVENT_TAKE_PICTURE = "TakePictureEvent".hashCode();

    public static final int EVENT_TAKE_PICTURE_ERROR = "TakePictureErrorEvent".hashCode();

    public static final int EVENT_SWITCH_BACK = "LensFacingBack".hashCode();

    public static final int EVENT_SWITCH_FRONT = "LensFacingFront".hashCode();

    public static final int EVENT_FLASH_MODE_AUTO = "FlashModeAuto".hashCode();

    public static final int EVENT_FLASH_MODE_ON = "FlashModeOn".hashCode();

    public static final int EVENT_FLASH_MODE_OFF = "FlashModeOff".hashCode();
}
