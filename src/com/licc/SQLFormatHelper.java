package com.licc;

/**
 *
 * @author linehanp
 */
public class SQLFormatHelper
{
  
  public String RemoveMySQLQuotes(String inputLine)
  {
    inputLine = inputLine.replace("`", "");
   
    return inputLine;
  }

}
