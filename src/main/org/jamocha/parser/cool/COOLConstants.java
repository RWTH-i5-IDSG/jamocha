/* Generated By:JJTree&JavaCC: Do not edit this line. COOLConstants.java */
package org.jamocha.parser.cool;

public interface COOLConstants {

  int EOF = 0;
  int SINGLE_LINE_COMMENT = 5;
  int LBRACE = 6;
  int RBRACE = 7;
  int GE1 = 8;
  int INTEGER = 9;
  int FLOAT = 10;
  int EXPONENT = 11;
  int STRING = 12;
  int INSTANCE = 13;
  int SFWILDCARD = 14;
  int MFWILDCARD = 15;
  int GLOBALVAR = 16;
  int DEFFACTS = 17;
  int DEFTEMPLATE = 18;
  int DEFTEMPLATE_CONSTRUCT = 19;
  int DEFRULE = 20;
  int DEFFUNCTION = 21;
  int DEFGENERIC = 22;
  int DEFMETHOD = 23;
  int DEFCLASS = 24;
  int DEFMESSAGEHANDLER = 25;
  int DEFINSTANCES = 26;
  int DEFMODULE = 27;
  int DEFGLOBAL = 28;
  int DEFAULT_ATR = 29;
  int DYNAMIC_ATR = 30;
  int ATR_DEFAULT = 31;
  int ATR_DERIVE = 32;
  int ATR_NONE = 33;
  int ATR_ALL = 34;
  int SLOT = 35;
  int SINGLE_SLOT = 36;
  int MULTISLOT = 37;
  int ARROW = 38;
  int DECLARE = 39;
  int SALIENCE = 40;
  int AUTOFOCUS = 41;
  int EXPORT = 42;
  int IMPORT = 43;
  int TYPE = 44;
  int TRUE = 45;
  int FALSE = 46;
  int NOT = 47;
  int AND = 48;
  int OR = 49;
  int OF = 50;
  int LOGICAL = 51;
  int TEST = 52;
  int EXISTS = 53;
  int FORALL = 54;
  int OBJECT = 55;
  int IS_A = 56;
  int ASSIGN = 57;
  int TILDE = 58;
  int AMPERSAND = 59;
  int LINE = 60;
  int COLON = 61;
  int EQUALS = 62;
  int VARIABLE_TYPE = 63;
  int SYMBOL_TYPE = 64;
  int STRING_TYPE = 65;
  int LEXEME_TYPE = 66;
  int INTEGER_TYPE = 67;
  int FLOAT_TYPE = 68;
  int NUMBER_TYPE = 69;
  int INSTANCE_NAME_TYPE = 70;
  int INSTANCE_ADDRESS_TYPE = 71;
  int INSTANCE_TYPE = 72;
  int EXTERNAL_ADDRESS_TYPE = 73;
  int FACT_ADDRESS_TYPE = 74;
  int ALLOWED_SYMBOLS = 75;
  int ALLOWED_STRINGS = 76;
  int ALLOWED_LEXEMES = 77;
  int ALLOWED_INTEGERS = 78;
  int ALLOWED_FLOATS = 79;
  int ALLOWED_NUMBERS = 80;
  int ALLOWED_INSTANCES = 81;
  int ALLOWED_CLASSES = 82;
  int ALLOWED_VALUES = 83;
  int RANGE = 84;
  int CARDINALITY = 85;
  int ROLE = 86;
  int CONCRETE = 87;
  int ABSTRACT = 88;
  int PATTERN_MATCH = 89;
  int REACTIVE = 90;
  int NON_REACTIVE = 91;
  int ACTIVE = 92;
  int STORAGE = 93;
  int LOCAL = 94;
  int SHARED = 95;
  int ACCESS = 96;
  int READ_WRITE = 97;
  int READ_ONLY = 98;
  int READ = 99;
  int WRITE = 100;
  int INITIALIZE_ONLY = 101;
  int PROPAGATION = 102;
  int INHERIT = 103;
  int NO_INHERIT = 104;
  int SOURCE = 105;
  int EXCLUSIVE = 106;
  int COMPOSITE = 107;
  int VISIBILITY = 108;
  int PRIVATE = 109;
  int PUBLIC = 110;
  int CREATE_ACCESSOR = 111;
  int OVERRIDE_MESSAGE = 112;
  int MESSAGE_HANDLER = 113;
  int PRIMARY = 114;
  int AROUND = 115;
  int BEFORE = 116;
  int AFTER = 117;
  int ASSERT = 118;
  int RETRACT = 119;
  int MODIFY = 120;
  int DUPLICATE = 121;
  int FACT_RELATION = 122;
  int FACT_SLOT_VALUE = 123;
  int IF = 124;
  int THEN = 125;
  int ELSE = 126;
  int WHILE = 127;
  int DO = 128;
  int LOOP_FOR_COUNT = 129;
  int SWITCH = 130;
  int CASE = 131;
  int VARSYMBOL = 132;
  int SINGLEVAR = 133;
  int MULTIVAR = 134;
  int SYMBOL = 135;

  int DEFAULT = 0;

  String[] tokenImage = {
    "<EOF>",
    "\" \"",
    "\"\\t\"",
    "\"\\n\"",
    "\"\\r\"",
    "<SINGLE_LINE_COMMENT>",
    "\"(\"",
    "\")\"",
    "\">=\"",
    "<INTEGER>",
    "<FLOAT>",
    "<EXPONENT>",
    "<STRING>",
    "<INSTANCE>",
    "\"?\"",
    "\"$?\"",
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
    "\"<-\"",
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
    "\"assert\"",
    "\"retract\"",
    "\"modify\"",
    "\"duplicate\"",
    "\"fact-relation\"",
    "\"fact-slot-value\"",
    "\"if\"",
    "\"then\"",
    "\"else\"",
    "\"while\"",
    "\"do\"",
    "\"loop-for-count\"",
    "\"switch\"",
    "\"case\"",
    "<VARSYMBOL>",
    "<SINGLEVAR>",
    "<MULTIVAR>",
    "<SYMBOL>",
  };

}
