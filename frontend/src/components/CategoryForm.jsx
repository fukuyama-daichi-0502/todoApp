import "./CategoryForm.css";

function CategoryForm({
    mode,
    name,
    colorCode,
    errorMessage,
    onNameChange,
    onColorCodeChange,
    onSubmit
}) {
    return(
        <form onSubmit = {onSubmit}>
            <table className = "formTable">
                <tr>
                    <th>
                        カテゴリ名
                    </th>
                    <td>
                        <input type = "text" value = {name} onChange = {onNameChange}/>
                    </td>
                    
                </tr>

                <tr>
                    <th>
                        カラー
                    </th>

                    <td>
                        <input type = "color" value = {colorCode} onChange = {onColorCodeChange} />
                    
                        <span>
                            {colorCode}
                        </span>
                    </td>
                    
                </tr>
            </table>

            {
                errorMessage && (
                    <p>{errorMessage}</p>
                )
            }

            <button ClassName = "formButton" type = "submit">
                {mode === "create" ? "登録" : "更新"}
            </button>
        </form>
    );
}

export default CategoryForm;