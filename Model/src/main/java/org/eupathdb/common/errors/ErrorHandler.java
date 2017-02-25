package org.eupathdb.common.errors;

import static org.eupathdb.common.errors.ErrorHandlerHelpers.getAttributeMapText;
import static org.eupathdb.common.errors.ErrorHandlerHelpers.valueOrDefault;
import static org.gusdb.fgputil.FormatUtil.NL;
import static org.gusdb.fgputil.FormatUtil.getInnerClassLog4jName;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.eupathdb.common.errors.ErrorHandlerHelpers.ErrorCategory;
import org.eupathdb.common.errors.ErrorHandlerHelpers.Stringifier;
import org.gusdb.fgputil.FormatUtil;
import org.gusdb.fgputil.Timer;
import org.gusdb.fgputil.db.pool.ConnectionPoolConfig;
import org.gusdb.fgputil.db.pool.DatabaseInstance;
import org.gusdb.fgputil.web.RequestData;
import org.gusdb.wdk.errors.ErrorBundle;
import org.gusdb.wdk.errors.ErrorContext;
import org.gusdb.wdk.model.WdkModel;

public class ErrorHandler {

  private static final Logger LOG = Logger.getLogger(ErrorHandler.class);

  private static final String ERROR_START = "##ERROR_START##";
  private static final String ERROR_END = "##ERROR_END##";
  private static final String SECTION_DIV = "************************************************" + NL;

  /**
   * Contains errors that matched filters
   */
  private static final class IgnoredErrorLog {
    private static final Logger _logger = Logger.getLogger(getInnerClassLog4jName(IgnoredErrorLog.class));
    private IgnoredErrorLog() {}
    public static Logger getLogger() { return _logger; }
  }

  /**
   * Contains errors that didn't match filters
   */
  private static final class RetainedErrorLog {
    private static final Logger _logger = Logger.getLogger(getInnerClassLog4jName(RetainedErrorLog.class));
    private RetainedErrorLog() {}
    public static Logger getLogger() { return _logger; }
  }

  private final Properties _filters;
  private final List<ErrorCategory> _categories;

  public ErrorHandler(Properties filters, List<ErrorCategory> categories) {
    _filters = filters;
    _categories = categories;
  }

  public void handleError(ErrorBundle errors, ErrorContext context) {

    // do nothing and return if no errors
    if (!errors.hasErrors())
      return;

    String searchText = getErrorText(errors, context);

    // check to see if this error matches a filter
    String matchedFilterKey = matchFilter(searchText, context.getRequestData(), _filters);

    String fullErrorText = new StringBuilder(ERROR_START).append(NL)
        .append("Matched Filter: ").append(matchedFilterKey).append(NL).append(NL)
        .append(searchText).append(NL).append(ERROR_END).append(NL).toString();

    // determine where to log this error based on context and filter match
    Logger errorLog = (matchedFilterKey != null ? IgnoredErrorLog.getLogger() : RetainedErrorLog.getLogger());
    errorLog.error(fullErrorText);

    if (matchedFilterKey == null && context.isSiteMonitored()) {
      // error passes through filters; email if it doesn't fall into an existing category
      ErrorCategory category = matchCategory(searchText, _categories);
      if (category == null || category.isFixed() || category.isEmailWorthy()) {
        // error did not match filter or category, category should have been fixed, or category should always send email
        sendMail(fullErrorText, context);
      }
    }
  }

  private static String getErrorText(ErrorBundle errors, ErrorContext context) {

    String doubleNewline = NL + NL;
    RequestData requestData = context.getRequestData();
    String errorUrl = getErrorUrl(requestData);
    WdkModel model = context.getWdkModel();
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    return new StringBuilder()

        .append(SECTION_DIV)
        .append("Date: ").append(dateFormat.format(context.getErrorDate())).append(NL)
        .append("Request URI: ").append(errorUrl == null ? "<unable to determine>" : errorUrl).append(NL)
        .append("Remote Host: ").append(valueOrDefault(requestData.getRemoteHost(), "<not set>")).append(NL)
        .append("Referred from: ").append(valueOrDefault(requestData.getReferrer(), "<not set>")).append(NL)
        .append("User Agent: ").append(requestData.getUserAgent()).append(NL)
        // "JkEnvVar SERVER_ADDR" is required in Apache configuration
        .append("Server Addr: ").append(valueOrDefault((String)requestData.getRequestAttribute("SERVER_ADDR"),
            "<not set> Is 'JkEnvVar SERVER_ADDR' set in the Apache configuration?")).append(NL)
        .append("Server Name: " ).append(valueOrDefault(requestData.getServerName(), "<unknown>")).append(NL)
        .append("Session ID: ").append(context.getMdcBundle().getShortSessionId()).append(NL)
        .append("Request ID: ").append(context.getMdcBundle().getRequestId()).append(NL)
        .append("Request duration at error: ").append(context.getMdcBundle().getRequestDuration()).append(NL)
        .append("Date of last webapp reload: ").append(dateFormat.format(new Date(model.getStartupTime()))).append(NL)
        .append("Time since last webapp reload: ").append(
            Timer.getDurationString(context.getErrorDate().getTime() - model.getStartupTime())).append(NL)
        .append("Log4j Marker: ").append(context.getLogMarker()).append(doubleNewline)

        .append(SECTION_DIV)
        .append("Project ID: ").append(model.getProjectId()).append(NL)
        .append("User DB: ").append(getDbInfo(model.getUserDb())).append(NL)
        .append("App DB: ").append(getDbInfo(model.getAppDb())).append(doubleNewline)

        .append(SECTION_DIV)
        .append("Request Parameters (query string or posted form data)").append(doubleNewline)
        .append(getAttributeMapText(requestData.getTypedParamMap(), new Stringifier<String[]>() {
          @Override public String stringify(String[] value, String qualifier) {
            return FormatUtil.arrayToString(value);
          }})).append(NL)

        .append(SECTION_DIV)
        .append("Associated Request-Scope Attributes").append(doubleNewline)
        .append(getAttributeMapText(context.getRequestAttributeMap(), new Stringifier<Object>() {
          @Override public String stringify(Object value, String key) {
            return (key.toLowerCase().startsWith("email") || key.toLowerCase().startsWith("passw")) ?
                "*****" : value.toString();
          }})).append(NL)

        .append(SECTION_DIV)
        .append("Session Attributes").append(doubleNewline)
        .append(getAttributeMapText(context.getSessionAttributeMap())).append(NL)

        //.append(SECTION_DIV)
        //.append("ServletContext Attributes").append(doubleNewline)
        //.append(getAttributeMapText(context.getServletContextAttributes())).append(NL)

        .append(SECTION_DIV)
        .append("Struts Action Errors").append(doubleNewline)
        .append(errors.getActionErrorsAsText()).append(NL)

        .append(SECTION_DIV)
        .append("Exception Stack Trace").append(doubleNewline)
        .append(valueOrDefault(errors.getStackTraceAsText(), "")).append(NL)

        .toString();
  }

  private static String getDbInfo(DatabaseInstance db) {
    ConnectionPoolConfig config = db.getConfig();
    return new StringBuilder()
        .append(config.getLogin()).append("@").append(config.getConnectionUrl())
        .append(" | Connections: Active { max=").append(config.getMaxActive())
        .append(", current=").append(db.getActiveCount())
        .append(" }, Idle { range=[").append(config.getMinIdle())
        .append("..").append(config.getMaxIdle())
        .append("], current=").append(db.getIdleCount())
        .append(" }").toString();
  }

  private static String getErrorUrl(RequestData requestData) {
    String requestURI = (String) requestData.getRequestAttribute("javax.servlet.forward.request_uri");
    String queryString = (String) requestData.getRequestAttribute("javax.servlet.forward.query_string");
    return (requestURI == null ? null
        : requestData.getNoContextUrl() + requestURI + (queryString == null ? "" : "?" + queryString));
  }

  /**
   * Check for matches to filters. Filters are regular expressions in a property file. The file is optional.
   * In which case, no filtering is performed.
   * 
   * Matches are checked against the text of errors and stacktraces.
   * 
   * Property file example 1. A simple check for missing step ids.
   * 
   * noStepForUser = The Step #\\d+ of user .+ doesn't exist
   * 
   * Compound filtering can be configured with specific subkeys in the property file (the primary key is
   * always required).
   * 
   * Property file example 2. Filter when exceptions contain the words "twoPartName is null" and also the
   * referer is empty.
   * 
   * twoPartNameIsNull = twoPartName is null twoPartNameIsNull.referer =
   * 
   * Allowed subkeys are referer ip
   **/
  private static String matchFilter(String searchText, RequestData requestData, Properties filters) {

    LOG.debug("Will use the following text as filter input:\n" + searchText);

    Set<String> propertyNames = filters.stringPropertyNames();
    for (String key : propertyNames) {

      // don't check subkeys yet
      if (key.contains("."))
        continue;

      String regex = filters.getProperty(key);
      Pattern p = Pattern.compile(regex);
      Matcher m = p.matcher(searchText);

      LOG.debug("Checking against filter: " + regex);
      if (m.find()) {
        LOG.debug("Match!");
        /**
         * Found match for primary filter. Now check for additional matches from any subkey filters. Return on
         * first match.
         **/
        boolean checkedSubkeys = false;
        String refererFilter = filters.getProperty(key + ".referer");
        String ipFilter = filters.getProperty(key + ".ip");

        if (refererFilter != null) {
          checkedSubkeys = true;
          String referer = valueOrDefault(requestData.getReferrer(), "");
          if (refererFilter.equals(referer))
            return key + " = " + regex + " AND " + key + ".referer = " + refererFilter;
        }

        if (ipFilter != null) {
          checkedSubkeys = true;
          String remoteHost = valueOrDefault(requestData.getRemoteHost(), "");
          if (ipFilter.equals(remoteHost))
            return key + " = " + regex + " AND " + key + ".ip = " + ipFilter;
        }

        // subkeys were checked and no matches in subkeys,
        // so match is not sufficient to filter
        if (checkedSubkeys) {
          LOG.debug("Matched primary filter but not any subkeys; moving to next filter.");
          continue;
        }

        // Otherwise no subkeys were checked (so primary filter match is sufficient)
        return key + " = " + regex;
      }
    }

    // did not match any filter
    return null;
  }

  private static ErrorCategory matchCategory(String searchText, List<ErrorCategory> categories) {
    for (ErrorCategory category : categories) {
      boolean matches = true;
      for (String matchString : category.getMatchStrings()) {
        // searchText must match each regex in category to be "found"
        if (searchText.indexOf(matchString) == -1) {
          matches = false;
          break;
        }
      }
      if (matches) {
        return category;
      }
    }
    return null;
  }

  private static void sendMail(String body, ErrorContext context) {

    String from = "tomcat@" + context.getRequestData().getServerName();
    List<String> recipients = context.getAdminEmails();
    String subject = context.getWdkModel().getProjectId() + " " + context.getRequestType().getLabel() +
        " Error - " + context.getRequestData().getRemoteHost();

    if (recipients.isEmpty()) {
      // Replaced SITE_ADMIN_EMAIL in model.prop with adminEmail attribute in model-config.xml
      LOG.error("adminEmail is not configured in model-config.xml; cannot send exception report.");
      return;
    }

    try {
      Properties props = new Properties();
      props.put("mail.smtp.host", "localhost");

      Session session = Session.getDefaultInstance(props, null);
      session.setDebug(false);

      Message msg = new MimeMessage(session);
      InternetAddress addressFrom = new InternetAddress(from);

      List<InternetAddress> addressList = new ArrayList<InternetAddress>();
      for (String address : recipients) {
        try {
          addressList.add(new InternetAddress(address));
        }
        catch (AddressException ae) {
          // ignore bad address
        }
      }
      InternetAddress[] addressTo = addressList.toArray(new InternetAddress[0]);

      msg.setRecipients(Message.RecipientType.TO, addressTo);
      msg.setFrom(addressFrom);
      msg.setSubject(subject);
      msg.setContent(body, "text/plain");

      Transport.send(msg);
    }
    catch (MessagingException me) {
      LOG.error(me);
    }
  }
}
