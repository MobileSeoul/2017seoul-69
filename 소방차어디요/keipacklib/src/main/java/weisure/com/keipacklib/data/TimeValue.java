package weisure.com.keipacklib.data;

/**
 * Created by chokyounglae on 15. 2. 6..
 */
public class TimeValue {
    private int _hour;      // 시
    private int _minute;    // 분
    private int _second;    // 초

    public TimeValue()
    {
        _hour = 0;
        _minute = 0;
        _second = 0;
    }

    public TimeValue(int hour, int minute, int second)
    {
        _hour = hour;
        _minute = minute;
        _second = second;
    }

    // 시간입력
    public void set_hour(int _hour) {
        this._hour = _hour;
    }

    // 분입력
    public void set_minute(int _minute) {
        if(_minute==0) this._minute = 0;
        else if(_minute > 0) {
            int hour = _minute / 60;
            this._minute = _minute % 60;
            this._hour += hour;
        }else{
            // 음수 사용 안함
            this._minute = 0;
        }
    }

    // 초입력
    public void set_second(int _second) {
        if(_second==0) {
            this._second = 0;
        }else if(_second>0) {
            int minute = _second / 60;
            this._second = _second % 60;
            set_minute(this._minute + minute);
        }else{
            // 음수 사용 안함
            this._second = 0;
        }
    }

    // 시간 가져오기
    public int get_hour() {
        return _hour;
    }

    // 분 가져오기
    public int get_minute() {
        return _minute;
    }

    // 초가져오기
    public int get_second() {
        return _second;
    }

    public double get_TotalHour() {
        return (this._hour + (double)this._minute / 60 + (double)this._second / 3600);
    }

    public double get_TotalMinute() {
        return (this._hour * 60 + this._minute + (double)this._second / 60);
    }

    public int get_TotalSecond() {
        return (this._hour * 3600 + this._minute * 60 + this._second);
    }

    public void add_hour(int h) {
        set_hour(get_hour() + h);
    }

    public void add_minute(int m) {
        set_minute(get_minute() + m);
    }

    public void add_second(int s) {
        set_second(get_second() + s);
    }

    // 현재 시를 문자열로 바꾸어 반환한다.
    public String hourToString()
    {
        String hour = String.format("%d", get_hour());

        // 항상 두자리 이상으로 맞춘다.
        // 예) 1hour 일경우 01hour로 변경
        if (hour.length() == 1)
        {
            hour = "0" + hour;
        }

        return hour;
    }

    // 현재 분을 문자열로 바꾸어 반환한다.
    public String minuteToString()
    {
        String minute = String.format("%d", get_minute());

        if (minute.length() == 1)
        {
            minute = "0" + minute;
        }

        return minute;
    }

    // 현재 초를 문자열로 바꾸어 반환한다.
    public String secondToString()
    {
        String second = String.format("%d", get_second());

        if (second.length() == 1)
        {
            second = "0" + second;
        }

        return second;
    }

    // 저장되어있는 시간을 문자열로 반환한다.
    // <param name="separate">시분초사이의 경계문자</param>
    public String TimeToString(String separate)
    {
        String h = hourToString();
        String m = minuteToString();
        String s = secondToString();

        return (h + separate + m + separate + s);
    }

    // 시간 값 초기화
    public void Clear()
    {
        this._hour = 0;
        this._minute = 0;
        this._second = 0;
    }

    public TimeValue CopyTo()
    {
        TimeValue copyTimeValue = new TimeValue(this._hour, this._minute, this._second);
        return copyTimeValue;
    }
}
