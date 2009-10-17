package geo;

/**
* <p><code>UnsignedChar</code> provides class-level convenience methods for
* ASCII-like operations typically performed in the C language with
* <code>unsigned char</code>.</p>
* @author Jerry Smith
* @version $Id: UnsignedChar.java 231 2006-01-15 01:57:48Z jsmith $
*/

public final class UnsignedChar {
  private static String lowerChars = "abcdefghijklmnopqrstuvwxyz";
  private static String upperChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

  private UnsignedChar() {
  }

  /**
  * <p>Tests the validity of a character for ASCII-like operations.</p>
  * @param ch The character.
  * @return Whether or not it is valid.
  */

  public static boolean isValid(char ch) {
    return Character.isLowerCase(ch) || Character.isUpperCase(ch);
  }

  /**
  * <p>Tests whether or not it a lowercase character.</p>
  * @param ch The character.
  * @return Whether or not it is lowercase.
  */

  public static boolean isLower(char ch) {
    return Character.isLowerCase(ch);
  }

  /**
  * <p>Tests whether or not it an uppercase character.</p>
  * @param ch The character.
  * @return Whether or not it is lowercase.
  */

  public static boolean isUpper(char ch) {
    return Character.isUpperCase(ch);
  }

  /**
  * <p>Gets the character at a particular offset from a start character.</p>
  * @param startChar The base, or reference, character.
  * @param offset The offset from the base character.
  * @return The character at the specified offset.
  */

  public static char getCharPlusOffset(char startChar, int offset) {
    if (!isValid(startChar)) {
      throw new RuntimeException("Character must be [a..b] or [A..B].");
    }
    String base = Character.isLowerCase(startChar) ? lowerChars : upperChars;
    int pos = base.indexOf(startChar) + offset;
    if (pos < 0 || pos > 25) {
      throw new IndexOutOfBoundsException(
        "Character plus offset must be [a..b] or [A..B].");
    }
    return base.charAt(pos);
  }

  /**
  * <p>Gets the next character in ASCII sequence.</p>
  * @param ch The character.
  * @return The next character in ASCII sequence.
  */

  public static char getNextChar(char ch) {
    return UnsignedChar.getCharPlusOffset(ch, 1);
  }

  /**
  * <p>Gets the previous character in ASCII sequence.</p>
  * @param ch The character.
  * @return The previous character in ASCII sequence.
  */

  public static char getPreviousChar(char ch) {
    return UnsignedChar.getCharPlusOffset(ch, -1);
  }

  /**
  * <p>Gets the character at a particular offset from a start character.</p>
  * @param ch The character.
  * @return The ASCII value.
  */

  public static int getASCIIValue(char ch) {
    if (!isValid(ch)) {
      throw new RuntimeException("Character must be [a..b] or [A..B].");
    }
    int offset = lowerChars.indexOf(Character.toLowerCase(ch));
    return offset + (Character.isLowerCase(ch) ? 97 : 65);
  }

  public static void main(String[] args) {
    if (args.length != 2) {
      System.out.println("Usage:  java " +
        UnsignedChar.class.getName() + " <char> <offset>");
      return;
    }
    char ch = args[0].charAt(0);
    int offset = Integer.parseInt(args[1], 10);
    System.out.println("Char '" + ch + "' + offset " + offset + " is '" +
      UnsignedChar.getCharPlusOffset(ch, offset) + "'.");
  }
}
