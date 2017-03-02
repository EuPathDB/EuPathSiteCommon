/**
 * Created by dfalke on 8/22/16.
 */
import { broadcast } from 'wdk-client/StaticDataUtils';

export const BASKETS_LOADED = 'eupathdb/basket'
export const QUICK_SEARCH_LOADED = 'eupathdb/quick-search-loaded'

export function loadBasketCounts() {
  return function run(dispatch, { wdkService }) {
    wdkService.getCurrentUser().then(user => {
      if (user.isGuest) return;
      wdkService.getBasketCounts().then(basketCounts => {
        dispatch(broadcast({
          type: BASKETS_LOADED,
          payload: { basketCounts }
        }));
      })
      .catch(error => {
        if (error.status !== 403) {
          console.error('Unexpected error while attempting to retrieve basket counts.', error);
        }
      });
    });
  };
}

/**
 * Load data for quick search
 * @param {Array<object>} questions An array of quick search spec objects.
 *    A spec object has two properties: `name`: the name of the questions,
 *    and `searchParam`: the name of the parameter to use for text box.
 * @return {run}
 */
export function loadQuickSearches(questions) {
  return function run(dispatch, { wdkService }) {
    let requests = questions.map(({ name, quickSearchParamName, quickSearchDisplayName }) => {
      return wdkService.sendRequest({
        method: 'GET',
        path: '/question/' + name,
        params: { expandParams: true },
        useCache: true
      }).then(question => {
        return Object.assign({}, question, { quickSearchParamName, quickSearchDisplayName })
      });
    });
    return Promise.all(requests).then(questions => {
      return dispatch(broadcast({
        type: QUICK_SEARCH_LOADED,
        payload: { questions }
      }));
    });
  }
}
