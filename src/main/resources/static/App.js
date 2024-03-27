export default class App {
    // Constructors:
    constructor() {
        this._employeeForm = document.querySelector("#employeeForm");
        this._searchForm = document.querySelector("#searchForm");
        this._tblEmployee = document.querySelector("#tblEmployee");



        this._employeeForm.querySelector("#btnInsert")
            .addEventListener(
                "click",
                this._onInsert.bind(this)
            );

        this._employeeForm.querySelector("#btnUpdate")
            .addEventListener(
                "click",
                this._onUpdate.bind(this)
            );

        this._searchForm.querySelector("#btnReload")
            .addEventListener(
                "click",
                this._loadEmployees.bind(this)
            );

        this._searchForm.querySelector("#btnSearch")
            .addEventListener(
                "click",
                this._onSearch.bind(this)
            );

        this._searchForm.querySelector("#btnCancelGenerate")
            .addEventListener(
                "click",
                this._onCancelGenerate.bind(this)
            );

        this._loadEmployees();
        this._setEmployee({ id: 0, name: "" });
    }

    // Methods:
    async _onInsert() {
        //Lấy element từ form
        this._txtID = document.getElementById("txtId");
        this._txtName = document.getElementById("txtName");

        //Lưu thông tin vào biến
        let txtId = this._txtID.value;
        let txtName = this._txtName.value;

        if (txtId == "" || txtName == "") {
            alert("Please enter id and name");
            return;
        }

        let data = {
            id: txtId,
            name: txtName
        };

        try {
            const response = await fetch("/spring-boot-webflux/employees", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(data)
            });

            const { success, message, code } = await response.json();
            if (success) {
                // Tạo thông báo thành công
                alert(`Insserted successfully: ${txtId} - ${txtName}`);
                await this._loadEmployees();
            }
            else {
                alert(`Code: ${code} - ${message}`);
            }
        } catch (error) {
            console.error(error);
        }

    }


    async _onUpdate() {
        //Lấy element từ form
        this._txtID = document.getElementById("txtId");
        this._txtName = document.getElementById("txtName");

        //Lưu thông tin vào biến
        let txtId = this._txtID.value;
        let txtName = this._txtName.value;

        if (txtId == "" || txtName == "") {
            alert("Please enter id and name");
            return;
        }

        let data = {
            id: txtId,
            name: txtName
        };

        try {
            const response = await fetch(`/spring-boot-webflux/employees/${txtId}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(data)
            });

            const { success, message, code } = await response.json();
            if (success) {
                // Tạo thông báo thành công
                alert(`Update successfully: ${txtId} - ${txtName}`);
                await this._loadEmployees();
            }
            else {
                alert(`Code: ${code} - ${message}`);
            }
        } catch (error) {
            console.error(error);
        }
    }

    async _onDelete(id) {
        try {
            let response = await fetch(`/spring-boot-webflux/employees/${id}`, {
                method: "DELETE", headers: {
                    "Content-Type": "application/json"
                }
            });

            const { success, message, code } = await response.json();

            if (success) {
                alert(`Delete successfully employee ${id}`);
                await this._loadEmployees();
            } else {
                alert(`Code: ${code} - ${message}`);
            }
        } catch (error) {
            console.error(error);
        }
    }

    async _loadEmployees() {
        try {
            const response = await fetch("/spring-boot-webflux/employees");

            const { result } = await response.json();

            await this._showEmployees(result);
        }
        catch (error) {
            console.error(error);
        }
    }

    async _showEmployees(employees) {
        const tbl = this._tblEmployee;

        for (const row of tbl.querySelectorAll("tr")) {
            if (row.id !== 'tblEmployeeHeaderRow') {
                tbl.removeChild(row);
            }
        }

        for (const { id, name } of employees) {
            const row = document.createElement("tr");

            const idCol = document.createElement("td");
            const nameCol = document.createElement("td");
            const actionCol = document.createElement("td");
            const btnUpdate = document.createElement("button");
            const btnDelete = document.createElement("button");

            idCol.textContent = id;
            nameCol.textContent = name;

            btnUpdate.textContent = "Update"
            btnUpdate.type = "button";
            btnUpdate.addEventListener("click", (() => {
                this._setEmployee({ id, name })
            }).bind(this));

            btnDelete.textContent = "Delete";
            btnDelete.type = "button";
            btnDelete.addEventListener("click", (() => {
                if (window.confirm(`Are you sure you want to delete this employee ${id} ?`)) {
                    this._onDelete(id);
                } else {
                    return;
                }
            }).bind(this))

            actionCol.appendChild(btnUpdate);
            actionCol.appendChild(btnDelete);

            row.appendChild(idCol);
            row.appendChild(nameCol);
            row.appendChild(actionCol);

            tbl.appendChild(row);
        }
    }

    async _onSearch() {
        const keyword = this._searchForm.querySelector("#txtKeyword").value;

        if (keyword === '') {
            alert("Keyword can't be empty!");
            return;
        }

        const response = await fetch(
            `/spring-boot-webflux/employees?keyword=${keyword}`
        );

        const { success, message, result } = await response.json();

        if (success) {
            this._showEmployees(result);
        }
        else {
            alert(message);
        }
    }

    async _onCancelGenerate() {
        const response = await fetch("/spring-boot-webflux/employees/generateEmployees",
        {
            method: "DELETE"
        })

        const { success, message } = await response.json();

        if (success) {
            alert("Employee auto generator cancelled successfully!");
        }
        else {
            alert(message);
        }
    }

    _setEmployee(employee) {
        if (!employee) {
            return;
        }

        this._employee = employee;

        this._employeeForm.querySelector("#txtId").value = this._employee.id;
        this._employeeForm.querySelector("#txtName").value = this._employee.name;
    }
}