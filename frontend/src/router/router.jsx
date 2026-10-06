import { createBrowserRouter } from "react-router-dom";

import TodoPage from "../views/TodoPage";
import TaskCreatePage from "../views/TaskCreatePage";
import TaskEditPage from "../views/TaskEditPage";
import CategoryPage from "../views/CategoryPage";
import CategoryCreatePage from "../views/CategoryCreatePage";

export const router = createBrowserRouter([
    {
        path: "/",
        element: <TodoPage />
    },
    {
        path: "/tasks/new",
        element: <TaskCreatePage />
    },
    {
        path: "/tasks/:id/edit",
        element: <TaskEditPage />
    },
    {
        path: "/categories",
        element: <CategoryPage />
    },
    {
        path: "/categories/new",
        element: <CategoryCreatePage />
    }
]);