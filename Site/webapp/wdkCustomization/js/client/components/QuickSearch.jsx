import { PropTypes } from 'react';
import {Tooltip} from 'wdk-client/Components';
import { wrappable } from 'wdk-client/ComponentUtils';

/**
 * Quick search boxes that appear in header
 */
function QuickSearch(props) {
  let { questions, webAppUrl } = props;

  return (
    <div id="quick-search" style={{display: 'flex', marginBottom: '12px', marginTop: '16px', height: '26px'}}>
      { questions == null ? null
      : questions.length == 0 ? (
        <div style={{
          width: '100%',
          height: '2em',
          background: 'gray'
        }}/>
      )
      : questions.map(question => {
        let { quickSearchParamName, quickSearchDisplayName } = question;
        let searchParam = question.parameters.find(p => p.name === quickSearchParamName);
        return (
          <div className="quick-search-item" style={{margin: '0 .4em'}} key={question.name}>
            <Tooltip content={searchParam.help}>
              <form name="questionForm" method="post" action={webAppUrl + '/processQuestionSetsFlat.do'}>
                <input type="hidden" name="questionFullName" value={question.name}/>
                <input type="hidden" name="questionSubmit" value="Get Answer"/>
                {question.parameters.map(parameter => {
                  if (parameter === searchParam) return null;
                  let { defaultValue, type, name } = parameter;
                  let typeTag = isStringParam(type) ? 'value' : 'array';
                  return (
                    <input key={`${typeTag}(${name})`} type="hidden" name={name} value={defaultValue}/>
                  );
                })}
                <b><a href={webAppUrl + '/showQuestion.do?questionFullName=' + question.name}>{quickSearchDisplayName}: </a></b>
                <input
                  type="text"
                  className="search-box"
                  defaultValue={searchParam.defaultValue}
                  name={'value(' + searchParam.name + ')'}
                />
                <input
                  name="go"
                  value="go"
                  type="image"
                  src={webAppUrl + '/images/mag_glass.png'}
                  alt="Click to search"
                  width="23"
                  height="23"
                  className="img_align_middle"
                />
              </form>
            </Tooltip>
          </div>
        );
      })}
    </div>
  )
}

let ParamPropType = PropTypes.shape({
  defaultValue: PropTypes.string.isRequired,
  help: PropTypes.string.isRequired,
  name: PropTypes.string.isRequired
});

QuickSearch.propTypes = {
  webAppUrl: PropTypes.string.isRequired,
  questions: PropTypes.arrayOf(PropTypes.shape({
    name: PropTypes.string.isRequired,
    displayName: PropTypes.string.isRequired,
    parameters: PropTypes.arrayOf(ParamPropType).isRequired,
    quickSearchParamName: PropTypes.string.isRequired,
    quickSearchDisplayName: PropTypes.string.isRequired
  }))
};

export default wrappable(QuickSearch);

/**
 * @param {Parameter} parameter
 * @return {boolean}
 */
function isStringParam(parameter) {
  return [ 'StringParam', 'TimestampParam' ].includes(parameter.type);
}
