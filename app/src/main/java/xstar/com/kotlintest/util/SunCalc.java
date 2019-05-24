package xstar.com.kotlintest.util;

/**
 * Created by xstar on 2016-12-08.
 */
public class SunCalc
{
	public static final String TAG = "SunCalc";

	/**
	 * 由日历时间y计算儒略历（格里历(公历)的前身 并不是公历）
	 *
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public double calcJD(int year, int month, int day)
	{
		if (month <= 2)
		{
			year = year - 1;
			month = month + 12;
		}
		double A = year / 100;
		double B = 2 - A + A / 4 + 1;

		return 365.25 * (year + 4716) + 30.6001 * (month + 1) + day + B - 1524.5;
	}

	/**
	 * 儒略历时间转换成儒略历2000年以来的几个世纪
	 *
	 * @param julianDay
	 * @return
	 */
	public double calcTimeJulianCent(double julianDay)
	{

		return (julianDay - 2451545) / 36525;
	}

	/**
	 * 儒略历2000年以来的几个世纪转换成儒略历时间
	 *
	 * @param t
	 * @return
	 */
	public double calcJDFromJulianCent(double t)
	{
		return t * 36525 + 2451545;
	}

	/**
	 * 太阳的几何平均经度，以度为单位
	 *
	 * @param t
	 *            儒略历2000年以来的几个世纪
	 * @return
	 */
	public double calcGeomMeanLongSun(double t)
	{
		double l0 = 280.46646 + t * (36000.76983 + 0.0003032 * t);
		do
		{
			if (l0 > 360) l0 -= 360;
			if (l0 < 0) l0 += 360;
		}
		while (l0 < 360 && l0 < 0);
		return l0;
	}

	/**
	 * 太阳的几何平均异常度
	 *
	 * @param t
	 * @return
	 */
	public double calcGeomMeanAnomalySun(double t)
	{
		return 357.52911 + t * (35999.05029 - 0.0001537 * t);
	}

	/**
	 * 计算无单位偏心率
	 *
	 * @param t
	 * @return
	 */
	public double calcEccentricityEarthOrbit(double t)
	{
		return 0.016708634 - t * (0.000042037 + 0.0000001267 * t);
	}

	/**
	 * 计算太阳的中心方程
	 *
	 * @param t
	 * @return 角度
	 */
	public double calcSunEqOfCenter(double t)
	{
		double m = calcGeomMeanAnomalySun(t);
		double mrad = Math.toRadians(m);
		double sinm = Math.sin(mrad);
		double sin2m = Math.sin(2 * mrad);
		double sin3m = Math.sin(3 * mrad);
		return sinm * (1.914602 - t * (0.004817 + 0.000014 * t)) + sin2m * (0.019993 - 0.000101 * t) + sin3m * 0.000289;
	}

	/**
	 * 计算真实太阳经度（赤经?）
	 *
	 * @param t
	 * @return
	 */
	public double calcSunTrueLong(double t)
	{
		double l = calcGeomMeanLongSun(t);
		double c = calcSunEqOfCenter(t);
		return l + c;
	}

	/**
	 * 计算太阳真实异常度
	 *
	 * @param t
	 * @return
	 */
	public double calcSunTrueAnomaly(double t)
	{
		double m = calcGeomMeanAnomalySun(t);
		double c = calcSunEqOfCenter(t);
		return m + c;
	}

	/**
	 * 太阳半径矢量 单位为1天文单位（约1.49亿公里日地品均距离）
	 *
	 * @param t
	 * @return
	 */
	public double calcSunRadVector(double t)
	{
		double v = calcSunTrueAnomaly(t);
		double e = calcEccentricityEarthOrbit(t);
		return (1.000001018 * (1 - e * e)) / (1 + e * Math.cos(Math.toRadians(v)));
	}

	/**
	 * 计算太阳的表观经度
	 *
	 * @param t
	 * @return
	 */
	public double calcSunApparentLong(double t)
	{

		double O = calcSunTrueLong(t);

		double omega = 125.04 - 1934.136 * t;
		return O - 0.00569 - 0.00478 * Math.sin(Math.toRadians(omega));
	}

	/**
	 * 计算黄道的平均倾角
	 *
	 * @param t
	 * @return
	 */
	public double calcMeanObliquityOfEcliptic(double t)
	{
		double seconds = 21.448 - t * (46.815 + t * (0.00059 - t * (0.001813)));
		return 23 + (26 + (seconds / 60)) / 60;
	}

	/***
	 * 计算黄道的校正倾斜度
	 *
	 * @param t
	 * @return
	 */
	public double calcObliquityCorrection(double t)
	{
		double e0 = calcMeanObliquityOfEcliptic(t);

		double omega = 125.04 - 1934.136 * t;
		return e0 + 0.00256 * Math.cos(Math.toRadians(omega));
	}

	/**
	 * 计算太阳的升高
	 *
	 * @param t
	 * @return
	 */
	public double calcSunRtAscension(double t)
	{
		double e = calcObliquityCorrection(t);
		double lambda = calcSunApparentLong(t);

		double tananum = (Math.cos(Math.toRadians(e)) * Math.sin(Math.toRadians(lambda)));
		double tanadenom = (Math.cos(Math.toRadians(lambda)));
		return Math.toDegrees(Math.atan2(tanadenom, tananum));
	}

	/**
	 * 计算太阳的偏角
	 *
	 * @param t
	 * @return
	 */
	public double calcSunDeclination(double t)
	{
		double e = calcObliquityCorrection(t);
		double lambda = calcSunApparentLong(t);

		double sint = Math.sin(Math.toRadians(e)) * Math.sin(Math.toRadians(lambda));
		return Math.toDegrees(Math.asin(sint));
	}

	/**
	 * 计算真太阳时间和平均太阳时间之间的差
	 *
	 * @param t
	 * @return
	 */
	public double calcEquationOfTime(double t)
	{
		double epsilon = calcObliquityCorrection(t);
		double l0 = calcGeomMeanLongSun(t);
		double e = calcEccentricityEarthOrbit(t);
		double m = calcGeomMeanAnomalySun(t);

		double y = Math.tan(Math.toRadians(epsilon) / 2);
		y = Math.pow(y, 2);

		double sin2l0 = Math.sin(2 * Math.toRadians(l0));
		double sinm = Math.sin(Math.toRadians(m));
		double cos2l0 = Math.cos(2 * Math.toRadians(l0));
		double sin4l0 = Math.sin(4 * Math.toRadians(l0));
		double sin2m = Math.sin(2 * Math.toRadians(m));

		return y * sin2l0 - 2 * e * sinm + 4 * e * y * sinm * cos2l0 - 0.5 * y * y * sin4l0 - 1.25 * e * e * sin2m;
	}

	/**
	 * 计算太阳在黎明时的小时角   纬度   用于用户选择的地平线以下的太阳能凹陷
	 *
	 * @param lat
	 *            纬度
	 * @param solarDec
	 *            太阳倾斜角
	 * @param solardepression
	 *            太阳地平线以下角度
	 * @return
	 */
	public double calcHourAngleDawn(double lat, double solarDec, double solardepression)
	{
		double latRad = Math.toRadians(lat);
		double sdRad = Math.toRadians(solarDec);

		double HAarg = (Math.cos(Math.toRadians(90 + solardepression)) / (Math.cos(latRad) * Math.cos(sdRad)) - Math.tan(latRad) * Math.tan(sdRad));

		double HA = (Math.acos(Math.cos(Math.toRadians(90 + solardepression)) / (Math.cos(latRad) * Math.cos(sdRad)) - Math.tan(latRad) * Math.tan(sdRad)));
		return HA;
	}

	/**
	 * 计算太阳在日出时对于纬度的小时角
	 *
	 * @param lat
	 *            纬度
	 * @param solarDec
	 *            太阳倾斜角
	 * @return
	 */
	public double calcHourAngleSunrise(double lat, double solarDec)
	{
		double latRad = Math.toRadians(lat);
		double sdRad = Math.toRadians(solarDec);

		double HAarg = (Math.cos(Math.toRadians(90.833)) / (Math.cos(latRad) * Math.cos(sdRad)) - Math.tan(latRad) * Math.tan(sdRad));

		double HA = (Math.acos(Math.cos(Math.toRadians(90.833)) / (Math.cos(latRad) * Math.cos(sdRad)) - Math.tan(latRad) * Math.tan(sdRad)));
		return HA;
	}

	/**
	 * 计算太阳在日落时的小时角纬度
	 *
	 * @param lat
	 * @param solarDec
	 * @return
	 */
	public double calcHourAngleSunset(double lat, double solarDec)
	{
		double latRad = Math.toRadians(lat);
		double sdRad = Math.toRadians(solarDec);

		double HAarg = (Math.cos(Math.toRadians(90.833)) / (Math.cos(latRad) * Math.cos(sdRad)) - Math.tan(latRad) * Math.tan(sdRad));

		double HA = (Math.acos(Math.cos(Math.toRadians(90.833)) / (Math.cos(latRad) * Math.cos(sdRad)) - Math.tan(latRad) * Math.tan(sdRad)));
		return -HA;
	}

	/**
	 * 计算太阳在黄昏时的小时角
	 *
	 * @param lat
	 * @param solarDec
	 * @param solardepression
	 * @return
	 */
	public double calcHourAngleDusk(double lat, double solarDec, double solardepression)
	{
		double latRad = Math.toRadians(lat);
		double sdRad = Math.toRadians(solarDec);

		double HAarg = (Math.cos(Math.toRadians(90 + solardepression)) / (Math.cos(latRad) * Math.cos(sdRad)) - Math.tan(latRad) * Math.tan(sdRad));

		double HA = (Math.acos(Math.cos(Math.toRadians(90 + solardepression)) / (Math.cos(latRad) * Math.cos(sdRad)) - Math.tan(latRad) * Math.tan(sdRad)));
		return -HA;
	}

	/**
	 * 计算黎明的通用协调时间（UTC）
	 *
	 * @param JD
	 * @param latitude
	 * @param longitude
	 * @param solardepression
	 * @return
	 */
	public double calcDawnUTC(double JD, double latitude, double longitude, double solardepression)
	{
		double t = calcTimeJulianCent(JD);

		// *** First pass to approximate sunrise

		double eqtime = calcEquationOfTime(t);
		double solarDec = calcSunDeclination(t);
		double hourangle = calcHourAngleSunrise(latitude, solarDec);

		double delta = longitude - Math.toDegrees(hourangle);
		double timeDiff = 4 * delta;
		// in minutes of time
		double timeUTC = 720 + timeDiff - eqtime;
		// in minutes

		// *** Second pass includes fractional jday in gamma calc

		double newt = calcTimeJulianCent(calcJDFromJulianCent(t) + timeUTC / 1440);
		eqtime = calcEquationOfTime(newt);
		solarDec = calcSunDeclination(newt);
		hourangle = calcHourAngleDawn(latitude, solarDec, solardepression);
		delta = longitude - Math.toDegrees(hourangle);
		timeDiff = 4 * delta;
		timeUTC = 720 + timeDiff - eqtime;
		// in minutes
		return timeUTC;
	}

	/**
	 * 计算日出的世界协调时间（UTC）
	 *
	 * @param JD
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	public double calcSunriseUTC(double JD, double latitude, double longitude)
	{
		double t = calcTimeJulianCent(JD);

		// *** First pass to approximate sunrise

		double eqtime = calcEquationOfTime(t);
		double solarDec = calcSunDeclination(t);
		double hourangle = calcHourAngleSunrise(latitude, solarDec);

		double delta = longitude - Math.toDegrees(hourangle);
		double timeDiff = 4 * delta;
		// in minutes of time
		double timeUTC = 720 + timeDiff - eqtime;
		// ' in minutes;

		// ' *** Second pass includes fractional jday in gamma calc

		double newt = calcTimeJulianCent(calcJDFromJulianCent(t) + timeUTC / 1440);
		eqtime = calcEquationOfTime(newt);
		solarDec = calcSunDeclination(newt);
		hourangle = calcHourAngleSunrise(latitude, solarDec);
		delta = longitude - Math.toDegrees(hourangle);
		timeDiff = 4 * delta;
		timeUTC = 720 + timeDiff - eqtime;
		// ' in minutes
		return timeUTC;
	}

	/**
	 * 计算太阳能的全球协调时间（UTC） 中午在给定的地点在给定的地点的中午
	 *
	 * @param t
	 * @param longitude
	 * @return
	 */
	public double calcSolNoonUTC(double t, double longitude)
	{
		double newt = calcTimeJulianCent(calcJDFromJulianCent(t) + 0.5 + longitude / 360);

		double eqtime = calcEquationOfTime(newt);
		double solarNoonDec = calcSunDeclination(newt);
		return 720 + (longitude * 4) - eqtime;
	}

	/**
	 * 计算日落的世界协调时间（UTC） 对于地球上给定位置的给定日子
	 *
	 * @param JD
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	public double calcSunsetUTC(double JD, double latitude, double longitude)
	{
		double t = calcTimeJulianCent(JD);

		// ' // First calculates sunrise and approx length of day

		double eqtime = calcEquationOfTime(t);
		double solarDec = calcSunDeclination(t);
		double hourangle = calcHourAngleSunset(latitude, solarDec);

		double delta = longitude - Math.toDegrees(hourangle);
		double timeDiff = 4 * delta;
		double timeUTC = 720 + timeDiff - eqtime;

		// ' // first pass used to include fractional day in gamma calc

		double newt = calcTimeJulianCent(calcJDFromJulianCent(t) + timeUTC / 1440);
		eqtime = calcEquationOfTime(newt);
		solarDec = calcSunDeclination(newt);
		hourangle = calcHourAngleSunset(latitude, solarDec);

		delta = longitude - Math.toDegrees(hourangle);
		timeDiff = 4 * delta;
		timeUTC = 720 + timeDiff - eqtime;
		return timeUTC;
	}

	/**
	 * 计算黄昏的通用协调时间（UTC） 对于地球上给定位置的给定日子
	 *
	 * @param JD
	 * @param latitude
	 * @param longitude
	 * @param solardepression
	 * @return
	 */
	public double calcDuskUTC(double JD, double latitude, double longitude, double solardepression)
	{
		double t = calcTimeJulianCent(JD);

		// ' // First calculates sunrise and approx length of day

		double eqtime = calcEquationOfTime(t);
		double solarDec = calcSunDeclination(t);
		double hourangle = calcHourAngleSunset(latitude, solarDec);

		double delta = longitude - Math.toDegrees(hourangle);
		double timeDiff = 4 * delta;
		double timeUTC = 720 + timeDiff - eqtime;

		// ' // first pass used to include fractional day in gamma calc

		double newt = calcTimeJulianCent(calcJDFromJulianCent(t) + timeUTC / 1440);
		eqtime = calcEquationOfTime(newt);
		solarDec = calcSunDeclination(newt);
		hourangle = calcHourAngleDusk(latitude, solarDec, solardepression);

		delta = longitude - Math.toDegrees(hourangle);
		timeDiff = 4 * delta;
		timeUTC = 720 + timeDiff - eqtime;
		return timeUTC;
	}

	/**
	 * 计算输入日期的黎明时间 和位置。 对于大于72度N和S的纬度，计算是 准确到10分钟内。 对于纬度小于+/- 72 精度约为一分钟。
	 *
	 * @param lat
	 * @param lon
	 *            经度对于输入单元的西半球是负的 '在调用名为的函数的电子表格中 '日出，solarnoon和日落。 这些函数转换
	 *            “经度对西半球的积极呼唤 '其他函数使用原始符号约定 '从NOAA的javascript代码。
	 * @param year
	 * @param month
	 * @param day
	 * @param timezone
	 * @param dlstime
	 * @param solardepression
	 * @return
	 */
	public double dawn(double lat, double lon, int year, int month, int day, double timezone, double dlstime, double solardepression)
	{
		double longitude = lon * -1;
		double latitude = lat;
		if (latitude > 89.8) latitude = 89.8;
		if (latitude < -89.8) latitude = -89.8;

		double JD = calcJD(year, month, day);

		// ' // Calculate sunrise for this date
		double riseTimeGMT = calcDawnUTC(JD, latitude, longitude, solardepression);

		// ' // adjust for time zone and daylight savings time in minutes
		double riseTimeLST = riseTimeGMT + (60 * timezone) + (dlstime * 60);

		// ' // convert to days
		double dawn = riseTimeLST / 1440;
		return dawn;
	}

	/**
	 * 计算输入日期的日出时间 和位置。 对于大于72度N和S的纬度，计算是 准确到10分钟内。 对于纬度小于+/- 72 精度约为一分钟。
	 *
	 * @param lat
	 * @param lon
	 *            经度对于输入单元的西半球是负的 '在调用名为的函数的电子表格中 '日出，solarnoon和日落。 这些函数转换
	 *            “经度对西半球的积极呼唤 '其他函数使用原始符号约定 '从NOAA的javascript代码。
	 * @param year
	 * @param month
	 * @param day
	 * @param timezone
	 * @param dlstime
	 * @return
	 */
	public double sunrise(double lat, double lon, int year, int month, int day, int timezone, int dlstime)
	{
		double longitude = lon * -1;
		double latitude = lat;
		if (latitude > 89.8) latitude = 89.8;
		if (latitude < -89.8) latitude = -89.8;

		double JD = calcJD(year, month, day);

		// ' // Calculate sunrise for this date
		double riseTimeGMT = calcSunriseUTC(JD, latitude, longitude);

		// ' // adjust for time zone and daylight savings time in minutes
		double riseTimeLST = riseTimeGMT + (60 * timezone) + (dlstime * 60);

		// ' // convert to days
		return riseTimeLST / 1440;
	}

	/**
	 * 计算输入日期的日中时间 和位置。 对于大于72度N和S的纬度，计算是 准确到10分钟内。 对于纬度小于+/- 72 精度约为一分钟。
	 *
	 * @param lat
	 * @param lon
	 * @param year
	 * @param month
	 * @param day
	 * @param timezone
	 * @param dlstime
	 * @return
	 */
	public double solarnoon(double lat, double lon, int year, int month, int day, double timezone, double dlstime)
	{
		double longitude = lon * -1;
		double latitude = lat;
		if (latitude > 89.8) latitude = 89.8;
		if (latitude < -89.8) latitude = -89.8;

		double JD = calcJD(year, month, day);
		double t = calcTimeJulianCent(JD);

		double newt = calcTimeJulianCent(calcJDFromJulianCent(t) + 0.5 + longitude / 360);

		double eqtime = calcEquationOfTime(newt);
		double solarNoonDec = calcSunDeclination(newt);
		double solNoonUTC = 720 + (longitude * 4) - eqtime;
		// ' // adjust for time zone and daylight savings time in minutes
		double solarnoon = solNoonUTC + (60 * timezone) + (dlstime * 60);

		// ' // convert to days
		solarnoon = solarnoon / 1440;

		return solarnoon;
	}

	/**
	 * 计算输入日期的日落时间 和位置。 对于大于72度N和S的纬度，计算是 准确到10分钟内。 对于纬度小于+/- 72 精度约为一分钟。
	 *
	 * @param lat
	 * @param lon
	 * @param year
	 * @param month
	 * @param day
	 * @param timezone
	 * @param dlstime
	 * @return
	 */
	public double sunset(double lat, double lon, int year, int month, int day, int timezone, int dlstime)
	{
		// TODO: 2016-12-08 计算输入日期的日落时间
		double longitude = lon * -1;
		double latitude = lat;
		if (latitude > 89.8) latitude = 89.8;
		if (latitude < -89.8) latitude = -89.8;

		double JD = calcJD(year, month, day);

		// ' // Calculate sunset for this date
		double setTimeGMT = calcSunsetUTC(JD, latitude, longitude);

		// ' // adjust for time zone and daylight savings time in minutes
		double setTimeLST = setTimeGMT + (60 * timezone) + (dlstime * 60);

		// ' // convert to days
		double sunset = setTimeLST / 1440;
		return sunset;
	}

	public double dusk(double lat, double lon, int year, int month, int day, int timezone, int dlstime, double solardepression)
	{
		double longitude = lon * -1;
		double latitude = lat;
		if (latitude > 89.8) latitude = 89.8;
		if (latitude < -89.8) latitude = -89.8;

		double JD = calcJD(year, month, day);

		// ' // Calculate sunset for this date
		double setTimeGMT = calcDuskUTC(JD, latitude, longitude, solardepression);

		// ' // adjust for time zone and daylight savings time in minutes
		double setTimeLST = setTimeGMT + (60 * timezone) + (dlstime * 60);

		// ' // convert to days
		double dusk = setTimeLST / 1440;
		return dusk;
	}

	/**
	 * 计算输入的太阳方位角（从北向度） 日期，时间和位置。 返回-999999如果比暗淡更暗
	 *
	 * solarelevation和solarazimuth函数是相同的 并且可以转换为将返回的VBA子例程   两个值。
	 * 
	 * @param lat
	 * @param lon
	 * @param year
	 * @param month
	 * @param day
	 * @param hours
	 * @param minutes
	 * @param seconds
	 * @param timezone
	 *            时区
	 * @param dlstime
	 *            是否夏令时
	 * @return
	 */
	public double solarazimuth(double lat, double lon, int year, int month, int day, int hours, int minutes, int seconds, int timezone, int dlstime)
	{

		double longitude = lon * -1;
		double latitude = lat;
		if (latitude > 89.8) latitude = 89.8;
		if (latitude < -89.8) latitude = -89.8;

		// 'change time zone to ppositive hours in western hemisphere
		double zone = timezone * -1;
		double daySavings = dlstime * 60;
		double hh = hours - (daySavings / 60);
		double mm = minutes;
		double ss = seconds;

		// '// timenow is GMT time for calculation in hours since 0Z
		double timenow = hh + mm / 60 + ss / 3600 + zone;

		double JD = calcJD(year, month, day);
		double t = calcTimeJulianCent(JD + timenow / 24);
		double R = calcSunRadVector(t);
		double alpha = calcSunRtAscension(t);
		double theta = calcSunDeclination(t);
		double Etime = calcEquationOfTime(t);

		double eqtime = Etime;
		double solarDec = theta;// in degrees
		double earthRadVec = R;

		double solarTimeFix = eqtime - 4 * longitude + 60 * zone;
		double trueSolarTime = hh * 60 + mm + ss / 60 + solarTimeFix;
		// '// in minutes

		while (trueSolarTime > 1440)
		{
			trueSolarTime = trueSolarTime - 1440;
		}

		double hourangle = trueSolarTime / 4 - 180;
		// '// Thanks to Louis Schwarzmayr for the next line:
		if (hourangle < -180) hourangle = hourangle + 360;

		double harad = Math.toRadians(hourangle);

		double csz = Math.sin(Math.toRadians(latitude)) * Math.sin(Math.toRadians(solarDec)) + Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(solarDec)) * Math.cos(harad);

		if (csz > 1) csz = 1;
		else if (csz < -1) csz = -1;

		double zenith = Math.toDegrees(Math.acos(csz));

		double azDenom = (Math.cos(Math.toRadians(latitude)) * Math.sin(Math.toRadians(zenith)));
		double azRad = 0;
		double azimuth = 0;
		if (Math.abs(azDenom) > 0.001)
		{
			azRad = ((Math.sin(Math.toRadians(latitude)) * Math.cos(Math.toRadians(zenith))) - Math.sin(Math.toRadians(solarDec))) / azDenom;
			if (Math.abs(azRad) > 1)
			{
				if (azRad < 0) azRad = -1;
				else azRad = 1;
			}
			azimuth = 180 - Math.toDegrees(Math.acos(azRad));

			if (hourangle > 0) azimuth = -azimuth;
		}
		else
		{
			if (latitude > 0) azimuth = 180;
			else azimuth = 0;
		}
		if (azimuth < 0) azimuth = azimuth + 360;

		double exoatmElevation = 90 - zenith;

		// 'beginning of complex expression commented out
		// ' If (exoatmElevation > 85#) Then
		// ' refractionCorrection = 0#
		// ' Else
		// ' te = Tan(degToRad(exoatmElevation))
		// ' If (exoatmElevation > 5#) Then
		// ' refractionCorrection = 58.1 / te - 0.07 / (te * te * te) + _
		// ' 0.000086 / (te * te * te * te * te)
		// ' ElseIf (exoatmElevation > -0.575) Then
		// ' refractionCorrection = 1735# + exoatmElevation * _
		// ' (-518.2 + exoatmElevation * (103.4 + _
		// ' exoatmElevation * (-12.79 + _
		// ' exoatmElevation * 0.711)))
		// ' Else
		// ' refractionCorrection = -20.774 / te
		// ' End If
		// ' refractionCorrection = refractionCorrection / 3600#
		// ' End If
		// 'end of complex expression
		//
		// 'beginning of simplified expression
		double refractionCorrection = 0;
		double te = 0;
		double step1 = 0;
		if (exoatmElevation > 85)
		{
			refractionCorrection = 0;
		}
		else
		{
			te = Math.tan(Math.toRadians(exoatmElevation));
			if (exoatmElevation > 5)
			{
				refractionCorrection = 58.1 / te - 0.07 / (te * te * te) + 0.000086 / (te * te * te * te * te);
			}
			else if (exoatmElevation > -0.575)
			{
				step1 = (-12.79 + exoatmElevation * 0.711);
				double step2 = (103.4 + exoatmElevation * (step1));
				double step3 = (-518.2 + exoatmElevation * (step2));
				refractionCorrection = 1735 + exoatmElevation * (step3);
			}
			else
			{
				refractionCorrection = -20.774 / te;
			}
			refractionCorrection = refractionCorrection / 3600;
		}
		// 'end of simplified expression
		//
		double solarzen = zenith - refractionCorrection;

		// ' If (solarZen < 108#)
		double solarazimuth = azimuth;
		// ' solarelevation = 90# - solarZen
		// ' If (solarZen < 90#) Then
		// ' coszen = Cos(degToRad(solarZen))
		// ' Else
		// ' coszen = 0#
		// ' End If
		// ' Else '// do not report az & el after astro twilight
		// ' solarazimuth = -999999
		// ' solarelevation = -999999
		// ' coszen = -999999
		// ' End If
		return solarazimuth;
	}

	/**
	 *
	 * @param lat
	 * @param lon
	 * @param year
	 * @param month
	 * @param day
	 * @param hours
	 * @param minutes
	 * @param seconds
	 * @param timezone
	 * @param dlstime
	 * @return
	 */
	public double solarelevation(double lat, double lon, int year, int month, int day, int hours, int minutes, int seconds, int timezone, int dlstime)
	{
		// ' change sign convention for longitude from negative to positive in
		// western hemisphere
		double longitude = lon * -1;
		double latitude = lat;
		if (latitude > 89.8) latitude = 89.8;
		if (latitude < -89.8) latitude = -89.8;

		// 'change time zone to ppositive hours in western hemisphere
		double zone = timezone * -1;
		double daySavings = dlstime * 60;
		double hh = hours - (daySavings / 60);
		double mm = minutes;
		double ss = seconds;

		// '// timenow is GMT time for calculation in hours since 0Z
		double timenow = hh + mm / 60 + ss / 3600 + zone;

		double JD = calcJD(year, month, day);
		double t = calcTimeJulianCent(JD + timenow / 24);
		double R = calcSunRadVector(t);
		double alpha = calcSunRtAscension(t);
		double theta = calcSunDeclination(t);
		double Etime = calcEquationOfTime(t);

		double eqtime = Etime;
		double solarDec = theta;// '// in degrees
		double earthRadVec = R;

		double solarTimeFix = eqtime - 4 * longitude + 60 * zone;
		double trueSolarTime = hh * 60 + mm + ss / 60 + solarTimeFix;
		// '// in minutes

		while (trueSolarTime > 1440)
		{
			trueSolarTime = trueSolarTime - 1440;
		}

		double hourangle = trueSolarTime / 4 - 180;
		// '// Thanks to Louis Schwarzmayr for the next line:
		if (hourangle < -180) hourangle = hourangle + 360;

		double harad = Math.toRadians(hourangle);

		double csz = Math.sin(Math.toRadians(latitude)) * Math.sin(Math.toRadians(solarDec)) + Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(solarDec)) * Math.cos(harad);

		if (csz > 1) csz = 1;
		else if (csz < -1) csz = -1;

		double zenith = Math.toDegrees(Math.acos(csz));

		double azDenom = (Math.cos(Math.toRadians(latitude)) * Math.sin(Math.toRadians(zenith)));

		double azRad = 0;
		double azimuth = 0;
		if (Math.abs(azDenom) > 0.001)
		{
			azRad = ((Math.sin(Math.toRadians(latitude)) * Math.cos(Math.toRadians(zenith))) - Math.sin(Math.toRadians(solarDec))) / azDenom;
			if (Math.abs(azRad) > 1)
			{
				if (azRad < 0) azRad = -1;
				else azRad = 1;
			}
			azimuth = 180 - Math.toDegrees(Math.acos(azRad));

			if (hourangle > 0) azimuth = -azimuth;
		}
		else
		{
			if (latitude > 0) azimuth = 180;
			else azimuth = 0;
		}
		if (azimuth < 0) azimuth = azimuth + 360;
		double exoatmElevation = 90 - zenith;

		double refractionCorrection = 0;
		double te = 0;
		double step1 = 0;
		double step2 = 0;
		double step3 = 0;
		if (exoatmElevation > 85) refractionCorrection = 0;
		else
		{
			te = Math.tan(Math.toRadians(exoatmElevation));
			if (exoatmElevation > 5)
			{
				refractionCorrection = 58.1 / te - 0.07 / (te * te * te) + 0.000086 / (te * te * te * te * te);
			}
			else if (exoatmElevation > -0.575)
			{
				step1 = (-12.79 + exoatmElevation * 0.711);
				step2 = (103.4 + exoatmElevation * (step1));
				step3 = (-518.2 + exoatmElevation * (step2));
				refractionCorrection = 1735 + exoatmElevation * (step3);
			}
			else
			{
				refractionCorrection = -20.774 / te;
			}
			refractionCorrection = refractionCorrection / 3600;
		}
		// 'end of simplified expression

		double solarzen = zenith - refractionCorrection;

		// ' If (solarZen < 108#) Then
		// ' solarazimuth = azimuth
		double solarelevation = 90 - solarzen;
		// ' If (solarZen < 90#) Then
		// ' coszen = Cos(degToRad(solarZen))
		// ' Else
		// ' coszen = 0#
		// ' End If
		// ' Else '// do not report az & el after astro twilight
		// ' solarazimuth = -999999
		// ' solarelevation = -999999
		// ' coszen = -999999
		// ' End If

		return solarelevation;
	}

	/**
	 *
	 * @param lat
	 * @param lon
	 * @param year
	 * @param month
	 * @param day
	 * @param hours
	 * @param minutes
	 * @param seconds
	 * @param timezone
	 * @param dlstime
	 * @return
	 */
	public double[] solarposition(double lat, double lon, int year, int month, int day, int hours, int minutes, int seconds, int timezone, int dlstime)
	{
		double longitude = lon * -1;
		double latitude = lat;
		if (latitude > 89.8) latitude = 89.8;
		if (latitude < -89.8) latitude = -89.8;

		// 'change time zone to ppositive hours in western hemisphere
		double zone = timezone * -1;
		double daySavings = dlstime * 60;
		double hh = hours - (daySavings / 60);
		double mm = minutes;
		double ss = seconds;

		// '// timenow is GMT time for calculation in hours since 0Z
		double timenow = hh + mm / 60 + ss / 3600 + zone;

		double JD = calcJD(year, month, day);
		double t = calcTimeJulianCent(JD + timenow / 24);
		double R = calcSunRadVector(t);
		double alpha = calcSunRtAscension(t);
		double theta = calcSunDeclination(t);
		double Etime = calcEquationOfTime(t);

		double eqtime = Etime;
		double solarDec = theta;// '// in degrees
		double earthRadVec = R;

		double solarTimeFix = eqtime - 4 * longitude + 60 * zone;
		double trueSolarTime = hh * 60 + mm + ss / 60 + solarTimeFix;
		// '// in minutes

		while (trueSolarTime > 1440)
		{
			trueSolarTime = trueSolarTime - 1440;
		}

		double hourangle = trueSolarTime / 4 - 180;
		// '// Thanks to Louis Schwarzmayr for the next line:
		if (hourangle < -180) hourangle = hourangle + 360;

		double harad = Math.toRadians(hourangle);

		double csz = Math.sin(Math.toRadians(latitude)) * Math.sin(Math.toRadians(solarDec)) + Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(solarDec)) * Math.cos(harad);

		if (csz > 1) csz = 1;
		else if (csz < -1) csz = -1;

		double zenith = Math.toDegrees(Math.acos(csz));

		double azDenom = (Math.cos(Math.toRadians(latitude)) * Math.sin(Math.toRadians(zenith)));

		double azRad = 0;
		double azimuth = 0;
		double exoatmElevation = 0;
		if (Math.abs(azDenom) > 0.001)
		{
			azRad = ((Math.sin(Math.toRadians(latitude)) * Math.cos(Math.toRadians(zenith))) - Math.sin(Math.toRadians(solarDec))) / azDenom;
			if (Math.abs(azRad) > 1)
			{
				if (azRad < 0) azRad = -1;
				else azRad = 1;
			}
			azimuth = 180 - Math.toDegrees(Math.acos(azRad));

			if (hourangle > 0) azimuth = -azimuth;
		}
		else
		{
			if (latitude > 0) azimuth = 180;
			else azimuth = 0;
		}
		if (azimuth < 0) azimuth = azimuth + 360;

		exoatmElevation = 90 - zenith;
		double refractionCorrection;
		double te;
		double step1;
		double step2;
		double step3;
		if (exoatmElevation > 85) refractionCorrection = 0;
		else
		{
			te = Math.tan(Math.toRadians(exoatmElevation));
			if (exoatmElevation > 5) refractionCorrection = 58.1 / te - 0.07 / (te * te * te) + 0.000086 / (te * te * te * te * te);
			else if (exoatmElevation > -0.575)
			{
				step1 = (-12.79 + exoatmElevation * 0.711);
				step2 = (103.4 + exoatmElevation * (step1));
				step3 = (-518.2 + exoatmElevation * (step2));
				refractionCorrection = 1735 + exoatmElevation * (step3);
			}
			else
			{
				refractionCorrection = -20.774 / te;
			}
			refractionCorrection = refractionCorrection / 3600;
		}
		// 'end of simplified expression

		double solarzen = zenith - refractionCorrection;

		// ' If (solarZen < 108#) Then
		double solarazimuth = azimuth;
		double solarelevation = 90 - solarzen;
		// ' If (solarZen < 90#) Then
		// ' coszen = Cos(degToRad(solarZen))
		// ' Else
		// ' coszen = 0#
		// ' End If
		// ' Else '// do not report az & el after astro twilight
		// ' solarazimuth = -999999
		// ' solarelevation = -999999
		// ' coszen = -999999
		// ' End If
		return new double[]
		{ solarazimuth, solarelevation };
	}
}
