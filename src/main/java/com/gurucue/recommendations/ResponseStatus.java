/*
 * This file is part of Guru Cue Search & Recommendation Engine.
 * Copyright (C) 2017 Guru Cue Ltd.
 *
 * Guru Cue Search & Recommendation Engine is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * Guru Cue Search & Recommendation Engine is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Guru Cue Search & Recommendation Engine. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package com.gurucue.recommendations;

import java.util.HashMap;

/**
 * Enumerates every possible status in a web service response, be that a RESTful service or part of an AJAX page.
 * The errors codes under 1000 mean success, everything else is a failure.
 * Note: when adding new values, take care that value-delimiting commas and the terminating semicolon stay
 * on their own lines, that way this is diff- and patch-friendly.
 */
public enum ResponseStatus {
    /**
     * Request processed OK.
     */
    OK(0, "OK"),
    /**
     * Request succeeded, but a username of an unknown consumer was provided, which was automatically added.
     */
    OK_CONSUMER_ADDED(1, "OK, consumer added"),
    /**
     * Request for adding a movie succeeded, but there were some warnings about errors that are deferred for manual resolution.
     */
    OK_MOVIE_ADDED_WITH_WARNINGS(2, "OK, movie inserted with warning"),
    /**
     * Unanticipated error occurred. Therefore no details are available in the error message.
     */
    UNKNOWN_ERROR(1001, "Unknown error"),
    PRODUCT_ID_MISSING(1002, "Product ID is missing"),
    NO_SUCH_PRODUCT_ID(1003, "There is no product with the given ID"),
    NO_PARTNER_ID(1004, "Partner ID is not available"),
    /**
     * Request contains a list of attribute values where two list
     * items define different original languages for the same
     * original value of the same attribute, but that is not
     * possible.
     */
    REQUEST_ATTRIBUTE_MULTIPLE_ORIGINAL_LANGUAGES(1005, "Multiple original languages specified for an attribute"),
    /**
     * Request contains an attribute value and language, but
     * the attribute is not translatable and thus the value
     * cannot be paired with a language.
     */
    REQUEST_ATTRIBUTE_LANGUAGE_PRESENT(1006, "Original language not supported with this attribute (not translatable)"),
    /**
     * Request contains a list of attribute values where two list
     * items define different values for the same attribute.
     */
    REQUEST_ATTRIBUTE_MULTIPLE_VALUES(1007, "Request contains multiple values for the same attribute"),
    PARTNER_ID_WRONG_TYPE(1008, "Partner ID is not an integer"),
    /**
     * Request contains multiple values for the same language translation of an attribute value.
     */
    REQUEST_TRANSLATION_DUPLICATE(1009, "Request contains multiple translations in the same language of an attribute value"),
    /**
     * Request contains an attribute value, but the given attribute is not defined for the product type.
     */
    REQUEST_ATTRIBUTE_ILLEGAL(1010, "Request contains an illegal attribute"),
    /**
     * When a partner wants to add a movie, but a movie with the specified ID is already registered with the partner.
     */
    MOVIE_EXISTS(1011, "The movie is already in the database"),
    /**
     * Request specified translations for an attribute value, but
     * the attribute is defined as not translatable and therefore
     * cannot take translations.
     */
    REQUEST_ATTRIBUTE_VALUE_NOT_TRANSLATABLE(1012, "Translations not supported with this attribute (not translatable)"),
    /**
     * Unknown format of the request. Request must be given either in
     * XML or in JSON syntax and specified with a Content-Type header.
     */
    UNKNOWN_REQUEST_FORMAT(1013, "The format of the request is not known: it must either be XML or JSON"),
    /**
     * Syntax error while parsing an XML request. The request violates XML syntax.
     */
    MALFORMED_XML(1014, "Malformed XML"),
    /**
     * Syntax error while parsing a JSON request. The request violates JSON syntax.
     */
    MALFORMED_JSON(1015, "Malformed JSON"),
    /**
     * Request is missing the movie title. A movie is uniquely
     * identified by its title in its original language and by its
     * production year.
     */
    REQUEST_WITHOUT_MOVIE_TITLE(1016, "Request is missing the movie title"),
    /**
     * Database already contains a (different) value for the specified product attribute.
     */
    ATTRIBUTE_NOT_MULTIVALUED(1017, "The attribute doesn't support multiple values"),
    /**
     * Attempted to insert an attribute value, but the attribute is
     * a code-type and the set of possible code identifiers for the
     * attribute doesn't contain the specified value.
     */
    ATTRIBUTE_CODE_RESTRICTED(1018, "The given value cannot be specified for the given attribute"),
    NO_SUCH_USER_ID(1019, "There is no user with the given ID"),
    /**
     * Original language of an original value for an attribute was given,
     * but the attribute does not support translations, or is a code-type
     * attribute and doesn't support (translatable) additions of new code.
     */
    ORIGINAL_LANGUAGE_NOT_APPLICABLE(1020, "Original language not applicaple for the attribute value"),
    /**
     * Original language of an original value for an attribute was given,
     * but the original value already exists and has assigned a different
     * original language from the given one.
     */
    ORIGINAL_LANGUAGE_DIFFERENT(1021, "The given original language for an attribute value differs from the already present original language"),
    USERNAME_MISSING(1022, "Missing username"),
    /**
     * A translated value for an attribute was given, but a translation
     * in the same language already exists and is different from the
     * given translation.
     */
    TRANSLATION_DIFFERENT(1023, "The given translation for an attribute value differs from the already present translation"),
    MOVIE_ID_MISSING(1024, "Movie ID missing"),
    MOVIE_IDS_MISSING(1025, "Movie IDs missing"),
    X509_CERTIFICATE_ERROR(1026, "Invalid client X.509 certificate"),
    /**
     * Invalid XML structure. XML syntax is okay, but the structure
     * does not adhere to the request composition rules for a specific web service
     * to which the request was sent.
     */
    INVALID_XML(1027, "XML data not valid"),
    /**
     * Invalid JSON structure. JSON syntax is okay, but the structure
     * does not adhere to the request composition rules for a specific web service
     * to which the request was sent.
     */
    INVALID_JSON(1028, "JSON data not valid"),
    /**
     * Original value of an attribute is <code>null</code> (empty).
     * @deprecated use ATTRIBUTE_VALUE_MISSING instead
     */
    @Deprecated
    ATTRIBUTE_ORIGINAL_VALUE_NULL(1029, "Original value of an attribute is missing"),
    PARTNER_MISSING(1030, "Partner is missing"),
    ILLEGAL_RATING_VALUE(1031, "Illegal rating value"),
    /**
     * An original language was specified for an attribute, but the
     * the processing logic forbids its presence. Typically at deletion.
     */
    ATTRIBUTE_LANGUAGE_FORBIDDEN(1032, "Specifying original language with the attribute value is forbidden"),
    ILLEGAL_TIMESTAMP_VALUE(1033, "Illegal timestamp value"),
    DATABASE_OPERATION_FAILED(1034, "Database operation failed"),
    /**
     * Translations were specified for an attribute, but the original
     * value was <code>null</code>, meaning deletion. Requesting
     * deletion, but providing translations, is not consistent.
     */
    ATTRIBUTE_TRANSLATIONS_ILLEGAL_WHEN_DELETING(1035, "Deletion of an original value requested, but translations also provided"),
    /**
     * Attempted to modify an attribute value, but it does not
     * exist in the database.
     * @deprecated use ATTRIBUTE_VALUE_MISSING instead
     */
    @Deprecated
    ATTRIBUTE_UPDATE_VALUE_MISSING(1036, "Cannot update a value for an attribute: the value does not exist"),
    /**
     * Request contains an attribute whose value must be an integer,
     * but the value given does not represent an integer.
     */
    ILLEGAL_INTEGER(1037, "Illegal integer value"),
    /**
     * When adding a product there are some attributes that are
     * required to be set, and at least one of these required
     * attributes is missing.
     */
    REQUIRED_ATTRIBUTE_MISSING(1038, "A required attribute is missing"),
    VALUE_IS_NULL(1039, "Value must not be null"),
    /**
     * Requested data about a movie that does not exist.
     */
    INVALID_MOVIE_ID(1040, "No movie with such ID"),
    RATING_IS_NULL(1041, "Rating is null"),
    TIMESTAMP_IS_NULL(1042, "Timestamp is null"),
    DOCUMENT_DEFINITION_ERROR(1043, "Document definition error"),
    DOCUMENT_PROCESSING_ERROR(1044, "Document processing error"),
    IDENTIFIER_MISSING(1045, "Identifier missing"),
    INVALID_IDENTIFIER(1046, "No property with the specified identifier"),
    USERNAME_EXISTS(1047, "Username already exists"),
    INVALID_SHA256(1048, "Invalid SHA-256 hash value"),
    LIST_EMPTY(1049, "A list is empty"),
    REQUEST_METHOD_NOT_SUPPORTED(1050, "This request method is not supported"),
    ATTRIBUTE_NAME_NOT_EXISTS(1051, "Attribute name is not recognized"),
    INVALID_LANGUAGE(1052, "No such language"),
    /**
     * @deprecated use ATTRIBUTE_LANGUAGE_MISSING or TRANSLATION_LANGUAGE_MISSING instead
     */
    @Deprecated
    LANGUAGE_MISSING(1053, "Language is missing"),
    /**
     * Attempted to delete a value for an attribute, but translations
     * were also given. Providing translations with the attribute you
     * want to delete is not consistent.
     */
    ATTRIBUTE_VALUE_TRANSLATIONS_NOT_EMPTY(1055, "Translations of an attribute value were given"),
    /**
     * Attempted to remove a required attribute from a movie.
     */
    DELETING_REQUIRED_ATTRIBUTE(1056, "Attempted to delete a required attribute"),
    /**
     * A request is missing the movie production year.
     */
    ATTRIBUTE_PRODUCTION_YEAR_MISSING(1057, "The request is missing the movie production year"),
    /**
     * An attribute original value is missing the original language.
     * A request has specified an original value for an attribute, but
     * the attribute is translatable and thus also requires the
     * specification of the original language, which is missing from
     * the request.
     * @deprecated use ATTRIBUTE_LANGUAGE_MISSING instead
     */
    @Deprecated
    ORIGINAL_LANGUAGE_MISSING(1058, "An attribute original value is missing the original language"),
    /**
     * Attempted to insert a new attribute value, but a value
     * for the attribute already exists in the database.
     */
    ATTRIBUTE_INSERT_VALUE_EXISTS(1059, "Cannot insert a value for an attribute: a value already exists"),
    /**
     * Request is missing the movie production year. A movie is uniquely
     * identified by its title in its original language and by its
     * production year.
     */
    REQUEST_WITHOUT_PRODUCTION_YEAR(1060, "Request is missing the movie production year."),
    /**
     * Request contains a translation of an attribute value in the same
     * language as the original attribute value, and thus should be exactly
     * the same, but it is not.
     */
    TRANSLATED_VALUE_NOT_EQUAL_TO_ORIGINAL(1061, "Cannot have a different translation of the original value in the same language."),
    /**
     * Recommender engine is not ready, RecommenderNotReadyException was caught. It can take
     * up to 5 minutes to prepare/calculate the recommender model.
     */
    RECOMMENDER_NOT_READY(1062, "Recommender is not ready yet, try again later."),
    /**
     * A single-valued attribute with a value was specified, but the specified product
     * was found not to have this value for the attribute.
     */
    VALUE_NOT_FOUND(1063, "The value for the specified attribute of the product was not found."),
    /**
     * The content marked as Base64 does not conform to the Base64 encoding specification.
     */
    INVALID_BASE64(1064, "Input is not a valid Base64 representation."),
    /**
     * The file does not exist.
     */
    FILE_DOES_NOT_EXIST(1065, "No such file."),
    /**
     * Could not open the file for writing.
     */
    CANNOT_WRITE_FILE(1066, "Cannot write to file."),
    /**
     * The given MIME-type was not recognized.
     */
    UNKNOWN_MIME_TYPE(1067, "Unrecognized file MIME-type"),
    /**
     * Request contains an attribute whose value must be a double,
     * but the value given does not represent a double.
     */
    ILLEGAL_DOUBLE(1068, "Illegal double value"),
    /**
     * Request contains an attribute whose value must be a boolean,
     * but the value given does not represent a boolean.
     */
    ILLEGAL_BOOLEAN(1069, "Illegal boolean value"),
    /**
     * Request contains event data whose identifier is not specified or it is empty or null.
     */
    NULL_CONSUMER_EVENT_DATA_TYPE_IDENTIFIER(1070, "The event data type identifier is missing"),
    /**
     * Request contains event data whose identifier wasn't recognized.
     * TODO: not used anymore, remove.
     */
    ILLEGAL_CONSUMER_EVENT_DATA_TYPE_IDENTIFIER(1071, "Illegal event data type identifier"),
    /**
     * Request contains event data whose value is not specified or it is null.
     * TODO: not used anymore, remove.
     */
    NULL_CONSUMER_EVENT_DATA_VALUE(1072, "The event data value is missing"),
    /**
     * Request contains event data where two or more instances have the same identifier.
     */
    DUPLICATE_CONSUMER_EVENT_DATA(1073, "Instances of event data exist with the same identifier"),
    /**
     * Request contains a tag that does not exist.
     */
    ILLEGAL_TAG(1074, "A tag does not exist"),
    /**
     * Request contains a consumer event type identifier that does not exist.
     */
    INVALID_CONSUMER_EVENT_TYPE(1075, "A consumer event type does not exist"),
    /**
     * Request contains a consumer event data type identifier that does not exist.
     */
    INVALID_CONSUMER_EVENT_DATA_TYPE(1076, "A consumer event data type does not exist"),
    /**
     * Request contains an attribute whose value must be a boolean,
     * but the value given does not represent a boolean.
     */
    ILLEGAL_DATE(1077, "Illegal date value"),
    /**
     * Request for a movie store/change doesn't contain the product code.
     */
    PRODUCT_CODE_MISSING(1078, "Product code is missing"),
    /**
     * A request specifies a couple of values having the same value for the same attribute.
     */
    DUPLICATE_ATTRIBUTE_VALUE(1079, "A duplicate attribute value has been specified"),
    /**
     * A request contains a translation which doesn't specify its language.
     */
    TRANSLATION_WITHOUT_LANGUAGE(1080, "A translation has been specified without providing its language"),
    /**
     * Additional attribute values are required because the given values match multiple products.
     */
    MULTIPLE_PRODUCTS_MATCH(1081, "Multiple products match the given search criteria"),
    /**
     * An internal error occurred, which was the result of a bug in the processing code. 
     */
    INTERNAL_PROCESSING_ERROR(1082, "Internal processing error; please report it"),
    /**
     * Request is missing the original value in an attribute value definition.
     */
    ATTRIBUTE_VALUE_MISSING(1083, "The original value of an attribute value is missing"),
    /**
     * Request is missing the original language in an attribute value definition.
     */
    ATTRIBUTE_LANGUAGE_MISSING(1084, "The original language of an attribute value is missing"),
    /**
     * Request selected an existing attribute value by specifying an original language
     * that is not the same as the original language of the existing attribute. Therefore
     * the attribute value was found by searching through translations, not original
     * values. But the request also specified an empty translation (which means translation
     * deletion) for the language that matches the original language, and that is
     * forbidden, because it implies value deletion. If you really want to delete the
     * value, then do it correctly by using the modification service and specifying the
     * <code>attribute_deletion</code> list containing the value.
     */
    TRANSLATION_WITH_ORIGINAL_LANGUAGE_IS_EMPTY(1085, "The specified translation deletion would delete the original value"),
    /**
     *  Attempted to add a tag for a product, that already exists.
     */
    TAG_EXISTS(1086, "The specified tag already exists"),
    /**
     * Attempted to add a null-value tag for a product, but at least one non-null-value tag has already been added.
     */
    TAG_CANNOT_ADD_NULL_AND_NONNULL(1087, "Cannot add the same tag once with a value and once with no value (null)"),
    /**
     * Attempted to add and remove the same tag, all in the same request.
     */
    TAG_CANNOT_ADD_AND_REMOVE(1088, "Cannot add and remove the same tag in a single request"),
    /**
     * Attempted to add a tag value, while the tag itself is being deleted.
     */
    TAG_CANNOT_ADD_VALUE_WHEN_DELETING_TAG(1089, "A tag value addition has been attempted, while the tag itself is being deleted")
    ,
    /**
     * A request has been made whose path part of the URL is too deep (too many sub-directories).
     */
    REQUEST_PATH_TOO_DEEP(1090, "The path part of the request URL is too deep, the URL is most probably incorrect")
    ,
    /**
     * A request has been made with a partner username that does not exist.
     */
    INVALID_PARTNER(1091, "The partner is not configured")
    ,
    /**
     * Syntax error while parsing the request.
     */
    MALFORMED_REQUEST(1092, "Malformed request")
    ,
    /**
     * A recommendations request specified an unknown recommender type.
     */
    NO_SUCH_RECOMMENDER(1093, "Recommender not found")
    ,
    /**
     * A request is missing an attribute identifier somewhere.
     */
    ATTRIBUTE_MISSING(1094, "Attribute is missing")
    ,
    /**
     * A request contains more than one change for a given attribute value.
     * It should contain only a single change, otherwise we don't know which change is the definitive one.
     */
    ATTRIBUTE_MULTIPLE_CHANGES(1095, "Multiple changes are requested for the same attribute")
    ,
    /**
     * A request contains more than one change for a given translation.
     * It should contain only a single change, otherwise we don't know which change is the definitive one.
     */
    TRANSLATION_MULTIPLE_CHANGES(1096, "Multiple changes are requested for the same translation")
    ,
    /**
     * A request contains a translation operation in the original language.
     */
    TRANSLATION_SET_IN_ORIGINAL_LANGUAGE(1097, "Cannot set or delete translation in the original language; instead delete and set the whole attribute")
    ,
    /**
     * A request contains an attribute with the original value or a translation that is the same
     * as the original value or a translation of a different instance of the same attribute.
     * Possible only with translatable multi-valued attributes.
     */
    ATTRIBUTE_SHARED_VALUE(1098, "An attribute contains a value or translation that is the same as a value or a translation of a different instance of the same attribute")
    ,
    /**
     * Request contains a product type identifier that does not exist.
     */
    INVALID_PRODUCT_TYPE(1099, "A product type does not exist")
    ,
    /**
     * Request for a product retrieval/store/modification/deletion doesn't contain the product type identifier.
     */
    PRODUCT_TYPE_MISSING(1100, "Product type is missing")
    ,
    /**
     * Consumer event is missing type.
     */
    EVENT_TYPE_MISSING(1101, "Event type is missing")
    ,
    /**
     * Request contains two or more attributes having the original value or
     * a translation with the same value for the same language.
     */
    MULTIPLE_ATTRIBUTES_WITH_SAME_TRANSLATION(1102, "There are two or more attributes having the same value/translation for a language")
    ,
    /**
     * There were translations specified for an attribute that is not
     * translatable.
     */
    TRANSLATIONS_FORBIDDEN(1103, "The attribute is not translatable, translations are forbidden")
    ,
    /**
     * An attribute's translation does not contain a value.
     */
    TRANSLATION_WITHOUT_VALUE(1104, "A translation has been specified without providing its value")
    ,
    /**
     * An attribute has two or more translations in the same language with different values.
     */
    MULTIPLE_DIFFERENT_TRANSLATIONS_IN_THE_SAME_LANGUAGE(1105, "Multiple different translation in the same language")
    ,
    /**
     * A single-valued attribute is specified multiple times, each time with a different value.
     */
    MULTIPLE_VALUES_OF_SINGLE_VALUED_ATTRIBUTE(1106, "Multiple values of single-valued attribute")
    ,
    /**
     * Request specified ID of a non-existent product.
     */
    INVALID_PRODUCT_ID(1107, "A product with the specified ID does not exist")
    ,
    /**
     * Request contains deletion of all translations of an attribute, meaning
     * that the attribute remains, but without a value. Delete the attribute
     * itself instead, if that was the intention.
     */
    TRANSLATION_NONE_REMAINS(1108, "Deleted all translations of a value; delete the attribute instead")
    ,
    /**
     * Recommendation request was for a recommender that need a reference
     * product to obtain results, but the product was not provided.
     */
    RECOMMENDER_NEEDS_PRODUCT(1109, "Requested recommendations from a recommender that needs a reference product, but no product was given")
    ,
    /**
     * A value of type timestamp interval was not specified correctly.
     */
    ILLEGAL_TIMESTAMP_INTERVAL_VALUE(1110, "Illegal timestamp interval value")
    ,
    /**
     * A product of an illegal product type was specified.
     */
    ILLEGAL_PRODUCT_TYPE(1111, "Illegal product-type")
    ,
    /**
     * The partner for which the recommendation request was done has no blender defined.
     */
    NO_BLENDER_FOR_PARTNER(1112, "No recommender exists for you")
    ,
    /**
     * Both the refProducts and product parameters were supplied with a recommendation request, but only one of them can be specified per request.
     */
    DONT_USE_REFPRODUCTS_AND_PRODUCT_SIMULTANEOUSLY(1113, "Do not use both \"refProducts\" and \"product\" simultaneously, only one is permitted (\"product\" is a singleton shortcut)")
    ,
    /**
     * A reference product was specified in a recommendation request (with either the refProduct or product parameter) that is not a GeneralVideoProduct instance.
     */
    REFPRODUCT_NOT_A_VIDEO_PRODUCT(1114, "A specified reference product is not a video or a tv-programme product")
    ,
    /**
     * A product was specified in a recommendation request (with the "products" parameter) that is not a GeneralVideoProduct instance.
     */
    PRODUCT_NOT_A_VIDEO_PRODUCT(1115, "A specified product is not a video or a tv-programme product")
    ,
    /**
     * A filter has been set as part of a blender or complex filter, but it requires arguments and none were provided.
     */
    FILTER_DEF_NO_ARGUMENTS(1116, "A filter requires arguments, but none were given")
    ,
    /**
     * A filter has been set as part of a blender or complex filter, but one of its arguments is not of a correct type.
     */
    FIlTER_DEF_ARGUMENT_WRONG_TYPE(1117, "Wrong argument type for a filter")
    ,
    /**
     * A filter has been set as part of a blender or complex filter, but the number of given arguments for it is wrong.
     */
    FILTER_DEF_WRONG_NUMBER_OF_ARGUMENTS(1118, "Wrong number of arguments for a filter")
    ,
    /**
     * A null was encountered while parsing an argument value for a filter.
     */
    FILTER_DEF_ARG_IS_NULL(1119, "An argument is null")
    ,
    /**
     * Required argument type is long, but the encountered argument is of some other type.
     */
    FILTER_DEF_ARG_IS_NOT_LONG(1120, "An argument is not a long")
    ,
    /**
     * Required argument type is an array of strings, but the encountered argument is of some other type.
     */
    FILTER_DEF_ARG_IS_NOT_STRING_ARRAY(1120, "An argument is not an array of strings")
    ,
    /**
     * Required argument type is integer, but the encountered argument is of some other type.
     */
    FILTER_DEF_ARG_IS_NOT_INT(1121, "An argument is not an integer")
    ,
    /**
     * Required argument type is a stateless filter, but the encountered argument is of some other type.
     */
    FILTER_DEF_ARG_IS_NOT_STATELESS_FILTER(1122, "An argument is not a stateless filter")
    ,
    /**
     * Required argument type is boolean, but the encountered argument is of some other type.
     */
    FILTER_DEF_ARG_IS_NOT_BOOL(1123, "An argument is not a boolean")
    ,
    /**
     * Filter with the specified identifier does not exist.
     */
    FILTER_DEF_FILTER_DOES_NOT_EXIST(1124, "Filter with the specified name does not exist")
    ,
    /**
     * Required argument type is String, but the encountered argument is of some other type.
     */
    FILTER_DEF_ARG_IS_NOT_STRING(1125, "An argument is not a String")
    ,
    /**
     * Occurs when using the advanced mode to define a blender or filter body, and there is an error in the code.
     */
    FILTER_DEF_BODY_PARSE_ERROR(1126, "Error parsing the code")
    ;


    private final Integer code;
    private final String description;

    ResponseStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
    
    private static final HashMap<Integer, ResponseStatus> codeMap = new HashMap<Integer, ResponseStatus>();
    static {
        for (ResponseStatus v : values()) {
            codeMap.put(v.getCode(), v);
        }
    }
    
    public static ResponseStatus fromCode(Integer code) {
        return codeMap.get(code);
    }
}
