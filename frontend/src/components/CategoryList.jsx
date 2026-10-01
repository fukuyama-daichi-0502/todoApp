import "./CategoryList.css";

function CategoryList({
    categories,
    onDelete
}) {

    const handleDeleteClick = (
        category
    ) => {
        const confirmed = window.confirm(`カテゴリ「${category.name}」を削除しますか？\n` + "このカテゴリに属するタスクは「カテゴリなし」になります") 

        if (!confirmed){
            return;
        }

        onDelete(category.id);
    }

    return(
        <table className = "category-table">
            <thead className = "tableHead">
                <tr>
                    <th>
                        ID
                    </th>

                    <th>
                        カテゴリ名
                    </th>

                    <th>
                        カラー
                    </th>

                    <th>
                        削除
                    </th>
                </tr>
            </thead>

            <tbody>
                {categories.map((category) => (
                    <tr key = {category.id}>
                        <td>
                            {category.id}
                        </td>

                        <td>
                            {category.name}
                        </td>
                        
                        <td>
                            <span className = "category-color" style = {{backgroundColor: category.categoryColor}}/>

                            <span style = {{color: category.categoryColor}}>
                                {category.categoryColor}
                            </span>
                        </td>

                        <td>
                            <button type = "button" onClick = {() => handleDeleteClick(category)}>
                                削除
                            </button>
                        </td>
                    </tr>
                ))}
            </tbody>
        </table>
    )
}

export default CategoryList;