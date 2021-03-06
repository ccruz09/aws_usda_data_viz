import Vue from 'vue';
import axios from 'axios';

var analyticsStore = {
    state: {
        analytics: [
            {
                name: "No Plot",
                description: "Does not perform any analysis of data.",
                columns: [],
                xVars: [],
                yVars: [],
                // These booleans indicate whether the user is allowed to select more than 1 X or Y value
                multiSelectX: true,
                multiSelectY: true,
                selected: false
            },
            {
                name: "Plot",
                description: "A simple scatter plot.",
                //TODO: Load from back-end in the future
                columns: ["Year","USGECropPercentCorn","ValuePerAcre","OperatingCostsPerAcre","SeedCostsPerAcre"],
                xVars: [],
                yVars: [],
                // These booleans indicate whether the user is allowed to select more than 1 X or Y value
                multiSelectX: true,
                multiSelectY: false,
                selected: false
            },
            {
                name: "Regression",
                description: "A linear regression.",
                //TODO:  Load from back-end in the future
                columns: ["Year","USGECropPercentCorn","ValuePerAcre","OperatingCostsPerAcre","SeedCostsPerAcre"],
                xVars: [],
                yVars: [],
                // These booleans indicate whether the user is allowed to select more than 1 X or Y value
                multiSelectX: false,
                multiSelectY: false,
                selected: false
            },
            {
                name: "Summary",
                description: "A summary of the data using a matrix.",
                columns: [],
                xVars: [],
                yVars: [],
                multiSelectX: true,
                multiSelectY: true,
                selected: false
            },
            {
                name: "TBD",
                description: "Currently unsupported.",
                unselectable: true,
                selected: false
            }
        ]
    },
    getters: {
        analytics: state => state.analytics,
        selectedAnalytics: state => state.analytics.filter( a => a.selected) || []
    },
    mutations: {
        updateAnalyticsSelection: function (state, analyticNames) {
            state.analytics.forEach(a => {
                a.selected = analyticNames.includes(a.name);
            });
        },
        updateAnalytic: function(state, newAnalytic) {
            let existAnalyticIndex = state.analytics.findIndex((item) => {
                return item.name === newAnalytic.name;
            });

            // Copy fields that exist in the new analytic and the existing one
            if (existAnalyticIndex >= 0) {
                for (let key in newAnalytic) {
                    if (state.analytics[existAnalyticIndex].hasOwnProperty(key)) {
                        Vue.set(state.analytics[existAnalyticIndex], key, newAnalytic[key]);
                    }
                }
            }
        }
    },
    actions: {
        /**
         * @param context
         * @param payload object of the form:
         *   {
         *     analyticName: "name of analytic to execute",
         *     datasetFilePath: "path to file for the dataset",
         *     callback: {
         *        failure: function //handler for failed request,
         *        success: function //handler for successful request
         *     }
         *   }
         */
        requestAnalyticExec: function(context, payload) {
            axios.get('/analytics/' + payload.analyticName.toLowerCase(), {
                params: {
                    inputFile: payload.datasetFilePath
                }
            })
            .then(function (response) {
                payload.callback.success(response);
            })
            .catch(function (error) {
                payload.callback.failure(error);
            });
        }
    }
};

export default analyticsStore;