package weisure.com.keipacklib.graphic;

import android.content.res.Resources;

/**
 * Created by chokyounglae on 15. 2. 6..
 */
public final class DimensionUtil {
	public static int dpToPx(int dp)
	{
	    return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
	}

	public static int pxToDp(int px)
	{
	    return (int) (px / Resources.getSystem().getDisplayMetrics().density);
	}
}
