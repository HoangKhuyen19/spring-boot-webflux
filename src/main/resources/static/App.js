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

        this._loadEmployees();
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
            console.log(error);
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
            console.log(error);
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
            btnUpdate.addEventListener("click", () => {
                this._employeeForm.querySelector("#txtId").value = id;
                this._employeeForm.querySelector("#txtName").value = name;
            })

            btnDelete.textContent = "Delete";
            btnDelete.type = "button";

            actionCol.appendChild(btnUpdate);
            actionCol.appendChild(btnDelete);

            row.appendChild(idCol);
            row.appendChild(nameCol);
            row.appendChild(actionCol);

            tbl.appendChild(row);
        }
    }
}