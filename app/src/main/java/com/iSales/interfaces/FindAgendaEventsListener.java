package com.iSales.interfaces;

import com.iSales.remote.rest.AgendaEventsREST;

public interface FindAgendaEventsListener {
    void onFindAgendaEventsTaskComplete(AgendaEventsREST mAgendaEventsREST);
    void onSendAgendaEventsTaskComplete();
    void onDeleteAgendasTaskComplete();
}
