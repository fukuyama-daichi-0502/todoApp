import axios from "axios";

export const getApiErrorMessage = (error, defaultMessage) => {
    //axiosのエラーか確認
    if(!axios.isAxiosError(error)){
        return defaultMessage;
    }
    
    if(error.response){
        return error.response.data?.message??defaultMessage;
    }

    if(error.request){
        return "サーバーに接続できませんでした";
    }

    return defaultMessage
}