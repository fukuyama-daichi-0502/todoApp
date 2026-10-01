export const formatDateTime = (dateTime) => {
    if(!dateTime){
        return"";
    }

    const date = new Date(dateTime);

    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2,"0");
    const day = String(date.getDate()).padStart(2,"0");

    const hour = String(date.getHours()).padStart(2,"0");
    const minute = String(date.getMinutes()).padStart(2,"0");
    const second = String(date.getSeconds()).padStart(2,"0");

    return `${year}/${month}/${day} ${hour}:${minute}:${second}`;
};

export const formatDate = (date) => {
    if(!date){
        return"";
    }

    const convDate = new Date(date);

    const year = convDate.getFullYear();
    const month = String(convDate.getMonth() + 1).padStart(2,"0");
    const day = String(convDate.getDate()).padStart(2,"0");

    return `${year}/${month}/${day}`;
};