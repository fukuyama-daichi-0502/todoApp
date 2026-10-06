import "./TodoList.css";
import { formatDateTime, formatDate } from "../formatters/dateTimeFormatter";
import { Link } from "react-router-dom";
import { DragDropProvider } from "@dnd-kit/react";
import {isSortable, useSortable} from "@dnd-kit/react/sortable";
import { useState } from "react";

function SortableTaskRow({
    task,
    index,
    today,
    isReorderDisabled,
    onDelete,
    onStatusChange,
    isDescriptionExpanded,
    onDescriptionToggle
}){

    const {
        ref,
        handleRef,
    } = useSortable({
        id: task.id,
        index,
        disabled: isReorderDisabled
    });

    //当日期限、期限切れかの確認
    const isFinished = task.status === "DONE" || task.status === "CANCELLED";

    const isOverdue = !isFinished && task.dueDate !== null && task.dueDate < today;

    const isDueToday = !isFinished && task.dueDate === today;

    const getTitleClassName = (status) => {

        if (status === "DONE") {
            return "task-title-done";
        }

        if (status === "CANCELLED") {
            return "task-title-cancelled";
        }

        return "";
        };

    const getRowClassName = (status) => {

        if (status === "DONE") {
            return "task-row-done";
        }else if (status === "CANCELLED") {
            return "";
        }else if (isDueToday) {
            return "task-row-due-today";
        }

        return "";
    };

    //優先度で表示するバッジの設定
    const getPriorityClassName = (priority) => {
        if (priority === "HIGH"){
            return "priority-high";
        } else if(priority === "MEDIUM" || priority === null){
            return "priority-medium";
        } else {
            return "priority-low";
        }
    }

    //詳細で常に表示する1行目を取得
    const descriptionFirstLine = task.description?.split(/\r?\n/)[0] ?? "";

    

    return(
        <tbody ref = {ref}>
            <tr className = {getRowClassName(task.status)} key={task.id}>
                <td className = "drag-handle-cell">
                    <button type = "button" ref = {handleRef} className = "drag-handle" disabled={isReorderDisabled} aria-label = "タスクを並び替える">
                        {
                            isReorderDisabled ? "⊘" : "☰"
                        }
                    </button>
                </td>
                <td>{task.id}</td>
                <td  className={getTitleClassName(task.status)}>{task.title}</td>
                <td>
                    <select className = {`status status-${task.status.toLowerCase()}`} value = {task.status} onChange = {(event) => onStatusChange(task.id, event.target.value)}>
                        <option value = "TODO">未着手</option>
                        <option value = "IN_PROGRESS">進行中</option>
                        <option value = "ON_HOLD">保留</option>
                        <option value = "DONE">完了</option>
                        <option value = "CANCELLED">中止</option>
                    </select>

                </td>
                <td>
                    <div className = "due-date-area">
                        <span className = {isOverdue ? "due-date-overdue" : ""}>{formatDate(task.dueDate) ?? "-"}</span>
                        {isOverdue && (
                            <span className = "overdue-label">
                                期限切れ
                            </span>
                        )}
                    </div>
                </td>
                <td>
                    <div className = "priority-badge">
                        <span className = {getPriorityClassName(task.priority)}>{task.priorityLabel}</span>
                    </div>
                    
                </td>
                <td>
                    {task.categoryName ? (
                    <dev className = "task-category">
                        <span className = "category-color" style = {{backgroundColor: task.categoryColor}}/>
                        <span className = "category-name" style = {{color: task.categoryColor}}>{task.categoryName}</span>
                    </dev>   
                    ) : (
                        "-"
                    )}

                </td>
                <td>{formatDateTime(task.createdAt)}</td>
                <td>
                    <Link className = "button" to = {`/tasks/${task.id}/edit`}>編集</Link>
                </td>
                <td>
                    <button type = "button" onClick = {() => onDelete(task.id)}>削除</button>
                </td>
            </tr>
            
            {task.description?.trim() &&(
                <tr className = {getRowClassName(task.status)}>
                        <td colspan = {10}>
                            <div className = {isDescriptionExpanded ? "task-description-text task-description-expanded" : "task-description-text task-description-collapsed"}>
                                    <span>タスク詳細：</span>
                                    <span className = "task-list-description-text">
                                        { isDescriptionExpanded ? task.description : descriptionFirstLine}
                                    </span>
                                
                                <button type = "button" className="description-toggle-button"onClick = {onDescriptionToggle}>
                                    { isDescriptionExpanded ? "閉じる" : "詳細を見る" }
                                </button>
                            </div>
                            
                        </td>
                </tr>
            )}
        </tbody>
        
    );
}

function TodoList({
    tasks,
    categories,
    today,
    titleSearch,
    statusFilter,
    dueDateFilter,
    priorityFilter,
    categoryFilter,
    // categoryOptions,
    isReorderDisabled,
    handleTitleSearchChange,
    handleStatusFilterChange,
    handleDueDateFilterChange,
    handlePriorityFilterChange,
    handleCategoryFilterChange,
    onDelete,
    onStatusChange,
    onTaskReorder
}) {

    const [expandedTaskId, setExpandedTaskId] = useState(null);
    

    const handleDragEnd = (event) => {

        if (isReorderDisabled) {
            return;
        }

        if(event.canceled){
            return;
        }

        const { source }  = event.operation;

        if(!isSortable(source)){
            return;
        }

        const{
            id,
            initialIndex,
            index
        } = source;
        
        
        if(initialIndex === index){
            return;
        }

        onTaskReorder(
            id,
            initialIndex,
            index
        );
    };

    //タスク詳細開閉処理  
    const handleDescriptionToggle = (taskId) => {
        setExpandedTaskId(currentTaskId => currentTaskId === taskId ? null : taskId);
    };

    return (            
        <div>

            {tasks.length === 0 ? (
                <>
                    <div calassName = "list-area">
                        <div className = "search">
                            <input type = "text" value = {titleSearch} onChange = {handleTitleSearchChange} placeholder = "検索"/>
                        </div>

                            <table className = "taskTable">
                                <thead className = "tableHead">
                                    <tr>
                                        <th>{/* ドラッグ列 */}</th>
                                        <th>ID</th>
                                        <th>タイトル</th>
                                        <th>
                                            <span>ステータス</span>

                                            <select className = {statusFilter !== "" ? "filter-select-active filter-select" : "filter-select"} value = {statusFilter} onChange = {handleStatusFilterChange}>
                                                <option value="">すべて</option>
                                                <option value="TODO">未着手</option>
                                                <option value="IN_PROGRESS">進行中</option>
                                                <option value="ON_HOLD">保留</option>
                                                <option value="DONE">完了</option>
                                                <option value="CANCELLED">中止</option>
                                            </select>
                                        </th>
                                        <th>
                                            <span>期限</span>

                                            <select className = {dueDateFilter !== "" ? "filter-select-active filter-select" : "filter-select"} value = {dueDateFilter} onChange = {handleDueDateFilterChange}>
                                                <option value = "">すべて</option>
                                                <option value = "TODAY">今日まで</option>
                                                <option value = "OVERDUE">期限切れ</option>
                                            </select>
                                        </th>
                                        <th>
                                            <span>優先度</span>

                                            <select className = {priorityFilter !== "" ? "filter-select-active filter-select" : "filter-select"} value = {priorityFilter} onChange = {handlePriorityFilterChange}>
                                                <option value = "">すべて</option>
                                                <option value="LOW">低</option>
                                                <option value="MEDIUM">中</option>
                                                <option value="HIGH">高</option>
                                            </select>
                                        </th>
                                        <th>
                                            <span>カテゴリ</span>

                                            <select className = {categoryFilter !== "" ? "filter-select-active filter-select" : "filter-select"} value = {categoryFilter} onChange = {handleCategoryFilterChange}>
                                                <option value = "">すべて</option>
                                                <option value = "NONE">-</option>

                                                {categories.map((category) => (
                                                    <option key = {category.id} value = {category.id}>{category.name}</option>
                                                ))}
                                            </select>
                                        </th>
                                        <th>登録日</th>
                                        <th>編集</th>
                                        <th>削除</th>
                                    </tr>

                                </thead>
                                
                            </table>
                    </div>
                    <p>タスクはありません</p>
            </>
            ): (
                <div calassName = "list-area">
                    <div className = "search">
                        <input type = "text" value = {titleSearch} onChange = {handleTitleSearchChange} placeholder = "タイトル検索"/>
                    </div>

                    <DragDropProvider onDragEnd = {handleDragEnd}>

                        <table className = "taskTable">
                            <thead className = "tableHead">
                                <tr>
                                    <th>{/* ドラッグ列 */}</th>
                                    <th>ID</th>
                                    <th>タイトル</th>
                                    <th>
                                        <span>ステータス</span>

                                        <select className = {statusFilter !== "" ? "filter-select-active filter-select" : "filter-select"} value = {statusFilter} onChange = {handleStatusFilterChange}>
                                            <option value="">すべて</option>
                                            <option value="TODO">未着手</option>
                                            <option value="IN_PROGRESS">進行中</option>
                                            <option value="ON_HOLD">保留</option>
                                            <option value="DONE">完了</option>
                                            <option value="CANCELLED">中止</option>
                                        </select>
                                    </th>
                                    <th>
                                        <span>期限</span>

                                        <select className = {dueDateFilter !== "" ? "filter-select-active filter-select" : "filter-select"} value = {dueDateFilter} onChange = {handleDueDateFilterChange}>
                                            <option value = "">すべて</option>
                                            <option value = "TODAY">今日まで</option>
                                            <option value = "OVERDUE">期限切れ</option>
                                        </select>
                                    </th>
                                    <th>
                                        <span>優先度</span>

                                        <select className = {priorityFilter !== "" ? "filter-select-active filter-select" : "filter-select"} value = {priorityFilter} onChange = {handlePriorityFilterChange}>
                                            <option value = "">すべて</option>
                                            <option value="LOW">低</option>
                                            <option value="MEDIUM">中</option>
                                            <option value="HIGH">高</option>
                                        </select>
                                    </th>
                                    <th>
                                        <span>カテゴリ</span>

                                        <select className = {categoryFilter !== "" ? "filter-select-active filter-select" : "filter-select"} value = {categoryFilter} onChange = {handleCategoryFilterChange}>
                                            <option value = "">すべて</option>
                                            <option value = "NONE">-</option>

                                            {categories.map((category) => (
                                                <option key = {category.id} value = {category.id}>{category.name}</option>
                                            ))}
                                        </select>
                                    </th>
                                    <th>登録日</th>
                                    <th>編集</th>
                                    <th>削除</th>
                                </tr>

                            </thead>
                            
                                {tasks.map((task, index) => (
                                    <SortableTaskRow 
                                        key = {task.id}
                                        task = {task}
                                        index = {index}
                                        today = {today}
                                        isReorderDisabled = {isReorderDisabled}
                                        onDelete = {onDelete}
                                        onStatusChange = {onStatusChange}
                                        isDescriptionExpanded = {expandedTaskId === task.id}
                                        onDescriptionToggle = {() => handleDescriptionToggle(task.id)}
                                    />    
                                ))}
                        </table>
                    </DragDropProvider>
                </div>
            )}
        </div>
    );
}

export default TodoList;