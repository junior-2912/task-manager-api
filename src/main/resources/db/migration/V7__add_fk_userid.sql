alter table public.task
    add constraint fk_task_user
        foreign key (user_id) references public.users(id);
