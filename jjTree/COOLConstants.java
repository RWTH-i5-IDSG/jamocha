/* Generated By:JJTree&JavaCC: Do not edit this line. COOLConstants.java */
public interface COOLConstants {

  int EOF = 0;
  int COMMENT_TEXT = 7;
  int END_COMMENT = 8;
  int LBRACE = 9;
  int RBRACE = 10;
  int GE1 = 11;
  int INTEGER = 12;
  int FLOAT = 13;
  int EXPONENT = 14;
  int STRING = 15;
  int INSTANCE = 16;
  int VARSYMBOL = 17;
  int SINGLEVAR = 18;
  int MULTIVAR = 19;
  int GLOBALVAR = 20;
  int DEFFACTS = 21;
  int DEFTEMPLATE = 22;
  int DEFTEMPLATE_CONSTRUCT = 23;
  int DEFRULE = 24;
  int DEFFUNCTION = 25;
  int DEFGENERIC = 26;
  int DEFMETHOD = 27;
  int DEFCLASS = 28;
  int DEFMESSAGEHANDLER = 29;
  int DEFINSTANCES = 30;
  int DEFMODULE = 31;
  int DEFGLOBAL = 32;
  int DEFAULT_ATR = 33;
  int DYNAMIC_ATR = 34;
  int ATR_DEFAULT = 35;
  int ATR_DERIVE = 36;
  int ATR_NONE = 37;
  int ATR_ALL = 38;
  int SLOT = 39;
  int SINGLE_SLOT = 40;
  int MULTISLOT = 41;
  int ARROW = 42;
  int DECLARE = 43;
  int SALIENCE = 44;
  int AUTOFOCUS = 45;
  int EXPORT = 46;
  int IMPORT = 47;
  int TYPE = 48;
  int TRUE = 49;
  int FALSE = 50;
  int NOT = 51;
  int AND = 52;
  int OR = 53;
  int OF = 54;
  int LOGICAL = 55;
  int TEST = 56;
  int EXISTS = 57;
  int FORALL = 58;
  int OBJECT = 59;
  int IS_A = 60;
  int NAME = 61;
  int ASSIGN = 62;
  int SFWILDCARD = 63;
  int MFWILDCARD = 64;
  int TILDE = 65;
  int AMPERSAND = 66;
  int LINE = 67;
  int COLON = 68;
  int EQUALS = 69;
  int VARIABLE_TYPE = 70;
  int SYMBOL_TYPE = 71;
  int STRING_TYPE = 72;
  int LEXEME_TYPE = 73;
  int INTEGER_TYPE = 74;
  int FLOAT_TYPE = 75;
  int NUMBER_TYPE = 76;
  int INSTANCE_NAME_TYPE = 77;
  int INSTANCE_ADDRESS_TYPE = 78;
  int INSTANCE_TYPE = 79;
  int EXTERNAL_ADDRESS_TYPE = 80;
  int FACT_ADDRESS_TYPE = 81;
  int ALLOWED_SYMBOLS = 82;
  int ALLOWED_STRINGS = 83;
  int ALLOWED_LEXEMES = 84;
  int ALLOWED_INTEGERS = 85;
  int ALLOWED_FLOATS = 86;
  int ALLOWED_NUMBERS = 87;
  int ALLOWED_INSTANCES = 88;
  int ALLOWED_CLASSES = 89;
  int ALLOWED_VALUES = 90;
  int RANGE = 91;
  int CARDINALITY = 92;
  int ROLE = 93;
  int CONCRETE = 94;
  int ABSTRACT = 95;
  int PATTERN_MATCH = 96;
  int REACTIVE = 97;
  int NON_REACTIVE = 98;
  int ACTIVE = 99;
  int STORAGE = 100;
  int LOCAL = 101;
  int SHARED = 102;
  int ACCESS = 103;
  int READ_WRITE = 104;
  int READ_ONLY = 105;
  int READ = 106;
  int WRITE = 107;
  int INITIALIZE_ONLY = 108;
  int PROPAGATION = 109;
  int INHERIT = 110;
  int NO_INHERIT = 111;
  int SOURCE = 112;
  int EXCLUSIVE = 113;
  int COMPOSITE = 114;
  int VISIBILITY = 115;
  int PRIVATE = 116;
  int PUBLIC = 117;
  int CREATE_ACCESSOR = 118;
  int OVERRIDE_MESSAGE = 119;
  int MESSAGE_HANDLER = 120;
  int PRIMARY = 121;
  int AROUND = 122;
  int BEFORE = 123;
  int AFTER = 124;
  int SYMBOL = 125;

  int DEFAULT = 0;
  int IN_COMMENT = 1;

  String[] tokenImage = {
    "<EOF>",
    "\" \"",
    "\"\\t\"",
    "\"\\n\"",
    "\"\\r\"",
    "\";\"",
    "<token of kind 6>",
    "<COMMENT_TEXT>",
    "<END_COMMENT>",
    "\"(\"",
    "\")\"",
    "\">=\"",
    "<INTEGER>",
    "<FLOAT>",
    "<EXPONENT>",
    "<STRING>",
    "<INSTANCE>",
    "<VARSYMBOL>",
    "<SINGLEVAR>",
    "<MULTIVAR>",
    "<GLOBALVAR>",
    "\"deffacts\"",
    "\"deftemplate\"",
    "\"deftemplate_construct\"",
    "\"defrule\"",
    "\"deffunction\"",
    "\"defgeneric\"",
    "\"defmethod\"",
    "\"defclass\"",
    "\"defmessage-handler\"",
    "\"definstances\"",
    "\"defmodule\"",
    "\"defglobal\"",
    "\"default\"",
    "\"default-dynamic\"",
    "\"?DEFAULT\"",
    "\"?DERIVE\"",
    "\"?NONE\"",
    "\"?ALL\"",
    "\"slot\"",
    "\"single-slot\"",
    "\"multislot\"",
    "\"=>\"",
    "\"declare\"",
    "\"salience\"",
    "\"auto-focus\"",
    "\"export\"",
    "\"import\"",
    "\"type\"",
    "<TRUE>",
    "<FALSE>",
    "\"not\"",
    "\"and\"",
    "\"or\"",
    "\"of\"",
    "\"logical\"",
    "\"test\"",
    "\"exists\"",
    "\"forall\"",
    "\"object\"",
    "\"is-a\"",
    "\"name\"",
    "\"<-\"",
    "\"?\"",
    "\"$?\"",
    "\"~\"",
    "\"&\"",
    "\"|\"",
    "\":\"",
    "\"=\"",
    "\"?VARIABLE\"",
    "\"SYMBOL\"",
    "\"STRING\"",
    "\"LEXEME\"",
    "\"INTEGER\"",
    "\"FLOAT\"",
    "\"NUMBER\"",
    "\"INSTANCE-NAME\"",
    "\"INSTANCE-ADDRESS\"",
    "\"INSTANCE\"",
    "\"EXTERNAL-ADDRESS\"",
    "\"FACT-ADDRESS\"",
    "\"allowed-symbols\"",
    "\"allowed-strings\"",
    "\"allowed-lexemes\"",
    "\"allowed-integers\"",
    "\"allowed-floats\"",
    "\"allowed-numbers\"",
    "\"allowed-instances\"",
    "\"allowed-classes\"",
    "\"allowed-values\"",
    "\"range\"",
    "\"cardinality\"",
    "\"role\"",
    "\"concrete\"",
    "\"abstract\"",
    "\"pattern-match\"",
    "\"reactive\"",
    "\"non-reactive\"",
    "\"active\"",
    "\"storage\"",
    "\"local\"",
    "\"shared\"",
    "\"access\"",
    "\"read-write\"",
    "\"read-only\"",
    "\"read\"",
    "\"write\"",
    "\"initialize-only\"",
    "\"propagation\"",
    "\"inherit\"",
    "\"no-inherit\"",
    "\"source\"",
    "\"exclusive\"",
    "\"composite\"",
    "\"visibility\"",
    "\"private\"",
    "\"public\"",
    "\"create-accessor\"",
    "\"override-message\"",
    "\"message-handler\"",
    "\"primary\"",
    "\"around\"",
    "\"before\"",
    "\"after\"",
    "<SYMBOL>",
  };

}
