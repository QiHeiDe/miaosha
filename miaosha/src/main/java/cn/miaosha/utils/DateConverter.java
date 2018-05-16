package cn.miaosha.utils;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;

public class DateConverter implements Converter<String, Date>{

	@Override
	public Date convert(String source) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
			Date date = format.parse(source);
			return date;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

}
