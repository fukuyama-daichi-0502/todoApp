import axios from "axios";

const Base_URL ="http://localhost:8080/api/tasks";

export const getTasks = async (searchConditions ={}) => {
    const params = {};
    
    if(searchConditions.title?.trim()){
        params.title = searchConditions.title.trim();
    }

    if(searchConditions.status){
        params.status = searchConditions.status;
    }

    if(searchConditions.dueDate){
        params.dueDate = searchConditions.dueDate;
    }

    if(searchConditions.priority){
        params.priority = searchConditions.priority;
    }

    if(searchConditions.category){
        params.category = searchConditions.category;
    }
    
    const response = await axios.get(Base_URL, {params: params});


    return response.data;
};

export const createTask = async (request) => {
    const response = await axios.post(Base_URL, request);

    return response.data;
};

export const createSubtask = async (parentId, subtask) => {
    const response = await axios.post(`${Base_URL}/${parentId}/subtasks`,subtask);

    return response.data;
}

export const updateTask = async(id, request) => {
    
    const response = await axios.put(`${Base_URL}/${id}`,request);

    return response.data;
};

export const updateTaskDescription = async(id, request) => {
    const response = await axios.patch(`${Base_URL}/${id}/description`, request);

    return response.data;
}

export const updateTaskStatus = async(id, status) => {
    const response = await axios.patch(
        `${Base_URL}/${id}/status`,
        {
            status: status
        }
    );

    return response.data;
};

export const getTask = async(id) => {
    const response = await axios.get(`${Base_URL}/${id}`);

    return response.data;
};

export const deleteTask = async(id) => {
    await axios.delete(`${Base_URL}/${id}`);
};

export const reorderTask = async (request) => {
    await axios.patch(`${Base_URL}/reorder`, request);
};

export const getSubtasks = async (parentId) => {
    const response = await axios.get(`${Base_URL}/${parentId}/subtasks`)

    return response.data;
}