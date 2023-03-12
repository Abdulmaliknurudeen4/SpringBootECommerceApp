CREATE TRIGGER calculate_category_closure_distance
    AFTER INSERT ON category_closure
    FOR EACH ROW
BEGIN
    DECLARE parent_distance INT;
    SELECT distance INTO parent_distance FROM category_closure
    WHERE ancestor_id = NEW.ancestor_id AND descendant_id = NEW.descendant_id;

    IF parent_distance IS NOT NULL THEN
        SET NEW.distance = parent_distance + 1;
    ELSE
        SET NEW.distance = 1;
END IF;
END;
