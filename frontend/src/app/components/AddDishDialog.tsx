import { useState } from "react";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "./ui/dialog";
import { Button } from "./ui/button";
import { Input } from "./ui/input";
import { Textarea } from "./ui/textarea";
import { Label } from "./ui/label";
import { Badge } from "./ui/badge";
import { Plus, X } from "lucide-react";
import { Dish } from "./DishCard";

interface AddDishDialogProps {
  onAddDish: (dish: Omit<Dish, "id" | "rating">) => void;
}

export function AddDishDialog({ onAddDish }: AddDishDialogProps) {
  const [open, setOpen] = useState(false);
  const [formData, setFormData] = useState({
    name: "",
    restaurant: "",
    description: "",
    tags: [] as string[],
    image: ""
  });
  const [currentTag, setCurrentTag] = useState("");

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (formData.name && formData.restaurant ) {
      onAddDish({
        name: formData.name,
        restaurant: formData.restaurant,
        description: formData.description,
        tags: formData.tags,
        image: formData.image || `https://images.unsplash.com/photo-1565299624946-b28f40a0ca4b?w=400&h=300&fit=crop`,
        // price: 0,
        // location: "",
        // distance: 0
      });
      setFormData({
        name: "",
        restaurant: "",
        description: "",
        tags: [],
        image: ""
      });
      setOpen(false);
    }
  };

  const addTag = () => {
    if (currentTag && !formData.tags.includes(currentTag)) {
      setFormData(prev => ({ ...prev, tags: [...prev.tags, currentTag] }));
      setCurrentTag("");
    }
  };

  const removeTag = (tag: string) => {
    setFormData(prev => ({ ...prev, tags: prev.tags.filter(t => t !== tag) }));
  };

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button>
          <Plus className="h-4 w-4 mr-2" />
          Add Dish
        </Button>
      </DialogTrigger>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>Add New Dish</DialogTitle>
        </DialogHeader>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <Label htmlFor="dish-name">Dish Name *</Label>
            <Input
              id="dish-name"
              value={formData.name}
              onChange={(e) => setFormData(prev => ({ ...prev, name: e.target.value }))}
              required
            />
          </div>
          
          <div>
            <Label htmlFor="restaurant-name">Restaurant Name *</Label>
            <Input
              id="restaurant-name"
              value={formData.restaurant}
              onChange={(e) => setFormData(prev => ({ ...prev, restaurant: e.target.value }))}
              required
            />
          </div>
          
          <div>
            <Label htmlFor="description">Description</Label>
            <Textarea
              id="description"
              value={formData.description}
              onChange={(e) => setFormData(prev => ({ ...prev, description: e.target.value }))}
            />
          </div>
          
          {/*<div className="grid grid-cols-2 gap-4">*/}
          {/*  <div>*/}
          {/*    <Label htmlFor="price">Price</Label>*/}
          {/*    <Input*/}
          {/*      id="price"*/}
          {/*      type="number"*/}
          {/*      step="0.01"*/}
          {/*      value={formData.price}*/}
          {/*      onChange={(e) => setFormData(prev => ({ ...prev, price: e.target.value }))}*/}
          {/*      required*/}
          {/*    />*/}
          {/*  </div>*/}
          {/*  <div>*/}
          {/*    <Label htmlFor="distance">Distance (kilometers)</Label>*/}
          {/*    <Input*/}
          {/*      id="distance"*/}
          {/*      type="number"*/}
          {/*      step="0.1"*/}
          {/*      value={formData.distance}*/}
          {/*      onChange={(e) => setFormData(prev => ({ ...prev, distance: e.target.value }))}*/}
          {/*    />*/}
          {/*  </div>*/}
          {/*</div>*/}
          
          {/*<div>*/}
          {/*  <Label htmlFor="location">Location</Label>*/}
          {/*  <Input*/}
          {/*    id="location"*/}
          {/*    value={formData.location}*/}
          {/*    onChange={(e) => setFormData(prev => ({ ...prev, location: e.target.value }))}*/}
          {/*  />*/}
          {/*</div>*/}
          
          <div>
            <Label>Tags</Label>
            <div className="flex space-x-2 mb-2">
              <Input
                placeholder="Add tag"
                value={currentTag}
                onChange={(e) => setCurrentTag(e.target.value)}
                onKeyPress={(e) => e.key === 'Enter' && (e.preventDefault(), addTag())}
              />
              <Button type="button" onClick={addTag}  variant="outline" size="sm">Add</Button>
            </div>
            <div className="flex flex-wrap gap-1">
              {formData.tags.map((tag) => (
                <Badge key={tag} variant="secondary" className="gap-1 bg-gray-200">
                  {tag}
                  <button
                      className="p-0 m-0 text-sm leading-none cursor-pointer"
                      onClick={() => removeTag(tag)}
                  >×</button>
                </Badge>
              ))}
            </div>
          </div>
          
          <div>
            <Label htmlFor="image">Image URL</Label>
            <Input
              id="image"
              value={formData.image}
              onChange={(e) => setFormData(prev => ({ ...prev, image: e.target.value }))}
              placeholder="Optional - will use default if empty"
            />
          </div>
          
          <div className="flex justify-end space-x-2">
            <Button type="button" variant="outline" onClick={() => setOpen(false)}>
              Cancel
            </Button>
            <Button type="submit" variant="outline">Add Dish</Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
}