import { useState } from "react";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "./ui/dialog";
import { Button } from "./ui/button";
import { Input } from "./ui/input";
import { Textarea } from "./ui/textarea";
import { Label } from "./ui/label";
import { Badge } from "./ui/badge";
import { Plus, X } from "lucide-react";
import { Restaurant } from "./RestaurantCard";

interface AddRestaurantDialogProps {
  onAddRestaurant: (restaurant: Omit<Restaurant, "id" | "rating">) => void;
}

export function AddRestaurantDialog({ onAddRestaurant }: AddRestaurantDialogProps) {
  const [open, setOpen] = useState(false);
  const [formData, setFormData] = useState({
    name: "",
    cuisine: "",
    description: "",
    tags: [] as string[],
    image: "",
    // location: "",
    // distance: "",
    // hours: "",
    // phone: "",
    // priceRange: ""
  });
  const [currentTag, setCurrentTag] = useState("");

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (formData.name && formData.cuisine ) {
      onAddRestaurant({
        name: formData.name,
        cuisine: formData.cuisine,
        description: formData.description,
        tags: formData.tags,
        image: formData.image || `https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=400&h=300&fit=crop`,
        // location: formData.location,
        // distance: parseFloat(formData.distance) || 1,
        // hours: formData.hours || "9 AM - 9 PM",
        // phone: formData.phone || "(555) 123-4567",
        // priceRange: formData.priceRange || "$$"
      });
      setFormData({
        name: "",
        cuisine: "",
        description: "",
        tags: [],
        image: "",
        // location: "",
        // distance: "",
        // hours: "",
        // phone: "",
        // priceRange: ""
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
        <Button variant="outline">
          <Plus className="h-4 w-4 mr-2" />
          Add Restaurant
        </Button>
      </DialogTrigger>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>Add New Restaurant</DialogTitle>
        </DialogHeader>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <Label htmlFor="restaurant-name">Restaurant Name *</Label>
            <Input
              id="restaurant-name"
              value={formData.name}
              onChange={(e) => setFormData(prev => ({ ...prev, name: e.target.value }))}
              required
            />
          </div>
          
          <div>
            <Label htmlFor="cuisine">Cuisine Type *</Label>
            <Input
              id="cuisine"
              value={formData.cuisine}
              onChange={(e) => setFormData(prev => ({ ...prev, cuisine: e.target.value }))}
              required
            />
          </div>
          
          {/*<div>*/}
          {/*  <Label htmlFor="description">Description</Label>*/}
          {/*  <Textarea*/}
          {/*    id="description"*/}
          {/*    value={formData.description}*/}
          {/*    onChange={(e) => setFormData(prev => ({ ...prev, description: e.target.value }))}*/}
          {/*  />*/}
          {/*</div>*/}
          
          {/*<div className="grid grid-cols-2 gap-4">*/}
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
          {/*  <div>*/}
          {/*    <Label htmlFor="price-range">Price Range</Label>*/}
          {/*    <Input*/}
          {/*      id="price-range"*/}
          {/*      placeholder="e.g. $$"*/}
          {/*      value={formData.priceRange}*/}
          {/*      onChange={(e) => setFormData(prev => ({ ...prev, priceRange: e.target.value }))}*/}
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
          
          {/*<div className="grid grid-cols-2 gap-4">*/}
          {/*  <div>*/}
          {/*    <Label htmlFor="hours">Hours</Label>*/}
          {/*    <Input*/}
          {/*      id="hours"*/}
          {/*      placeholder="9 AM - 9 PM"*/}
          {/*      value={formData.hours}*/}
          {/*      onChange={(e) => setFormData(prev => ({ ...prev, hours: e.target.value }))}*/}
          {/*    />*/}
          {/*  </div>*/}
          {/*  <div>*/}
          {/*    <Label htmlFor="phone">Phone</Label>*/}
          {/*    <Input*/}
          {/*      id="phone"*/}
          {/*      placeholder="(555) 123-4567"*/}
          {/*      value={formData.phone}*/}
          {/*      onChange={(e) => setFormData(prev => ({ ...prev, phone: e.target.value }))}*/}
          {/*    />*/}
          {/*  </div>*/}
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
              <Button type="button" onClick={addTag} size="sm">Add</Button>
            </div>
            <div className="flex flex-wrap gap-1">
              {formData.tags.map((tag) => (
                <Badge key={tag} variant="secondary" className="gap-1">
                  {tag}
                  <X className="h-3 w-3 cursor-pointer" onClick={() => removeTag(tag)} />
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
            <Button type="submit">Add Restaurant</Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
}