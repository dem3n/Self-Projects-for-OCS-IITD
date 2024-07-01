import pandas as pd
from config import *
from preprocess import get_exp_based_df
from utils import *

from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestRegressor, ExtraTreesRegressor
from sklearn.svm import LinearSVR, NuSVR, SVR
from sklearn.tree import DecisionTreeRegressor, ExtraTreeRegressor
from sklearn.linear_model import LinearRegression, HuberRegressor
from sklearn.neighbors import KNeighborsRegressor

from catboost import CatBoostRegressor
from lightgbm import LGBMRegressor
from xgboost import XGBRegressor

if __name__ == "__main__":
    exps = [experiment1, experiment2, experiment3, experiment4,
            experiment5, experiment6, experiment7, experiment8, experiment9]
    exp_no = 0
    model_results = pd.DataFrame()
    for exp in exps:
        exp_no += 1
        df_x, df_y = get_exp_based_df(exp)
        train_x, test_x, train_y, test_y = train_test_split(
            df_x, df_y, test_size=0.2, random_state=0)
        test_x, val_x, test_y, val_y = train_test_split(
            test_x, test_y, test_size=0.5, random_state=0)

        print(train_x.shape, test_x.shape, train_y.shape, test_y.shape)

        algos = (LinearRegression, HuberRegressor, KNeighborsRegressor, LinearSVR, NuSVR,
                 SVR, DecisionTreeRegressor, ExtraTreeRegressor, RandomForestRegressor, ExtraTreesRegressor,
                 XGBRegressor, LGBMRegressor, CatBoostRegressor)

        params = {
            'silent': True
        }

        for algo in algos:
            model = algo()
            model_name = type(model).__name__
            if model_name == 'CatBoostRegressor':
                model = algo(**params)
            model.fit(train_x, train_y)

            model_results_train = get_scores(
                train_y, get_preds(model, train_x))
            model_results_val = get_scores(val_y, get_preds(model, val_x))
            model_results_test = get_scores(test_y, get_preds(model, test_x))
            data = {"Train": model_results_train,
                    "Val": model_results_val,
                    "Test": model_results_test}
            temp = pd.DataFrame(data, index=[f'Exp_{exp_no}_{model_name}'])
            model_results = model_results.append(temp)

    model_results.to_csv(
        f'../../data/output/Critical_Point/critical_point_results.csv')
