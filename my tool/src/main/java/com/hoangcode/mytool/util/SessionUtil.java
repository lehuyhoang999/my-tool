package com.hoangcode.mytool.util;

import java.util.Enumeration;
import javax.servlet.http.HttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public final class SessionUtil {

  private SessionUtil() {
  }

  public static void set(String key, Object obj) {
    getSession().setAttribute(key, obj);
  }

  public static void delete(String key) {
    getSession().removeAttribute(key);
  }

  public static <T> T get(String key) {
    return (T) getSession().getAttribute(key);
  }

  public static boolean isExists(String key) {
    Enumeration<String> sessionKeyEnums = getSession().getAttributeNames();
    while (sessionKeyEnums.hasMoreElements()) {
      String sessionKey = sessionKeyEnums.nextElement();
      if (sessionKey.equals(key)) {
        return true;
      }
    }

    return false;
  }


  private static HttpSession getSession() {
    ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    return attr.getRequest().getSession();
  }

}
