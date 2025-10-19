importPackage(Packages.database);
importPackage(java.lang);

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, sel) {
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }
    
    
    if (status == 0) {
        cm.dispose();
        cm.openNpcCustom(cm.getClient(), 9001102, "2023cntjrevent");
    }
}
