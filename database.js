const nReadLines = require('n-readlines');
const fs = require('fs');
const stu = require('./student.js');

function meetsConditions(student, conditions, table) {
    var keys = Object.keys(conditions);
    for (var k in keys) {
        let key = keys[k];
        let index = stu.get(key, table);
        if (conditions[key] !== student[index]) {
            return false;
        }
    }
    return true;
}

module.exports = {
    TABLE_EMAILS: 'data/emails.txt',

    TABLE_CONFIRMATIONS: 'data/confirmations.txt',

    select: function(conditions, limit, table) {
        let arr = [];
        let liner = new nReadLines(table);
        let lineBuff;
        while(lineBuff = liner.next()) {
            let line = lineBuff.toString();
            let student = line.split("~");
            if (meetsConditions(student, conditions, table)) {
                limit--;
                arr.push(student);
                if (limit === 0) {
                    break;
                }
            }
        }
        return arr;
    },

    delete: function(conditions, table) {
        let liner = new nReadLines(table);
        var lines = "";
        let lineBuff;
        while(lineBuff = liner.next()) {
            let line = lineBuff.toString();
            let student = line.split("~");
            if (!meetsConditions(student, conditions, table)) {
                lines += line + "\n";
            }
        }
        fs.writeFile(table, lines, function (err) {});
    },

    insert: function(values, table) {
        var content = "";
        for (var i = 0; i < values.length; i++) {
            content += values[i] + (i == values.length - 1 ? "\n" : "~");
        }
        fs.appendFile(table, content, function (err) {});
    }
};